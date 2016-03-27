(ns soul-counter.core
  (:require [clojure.string :as string])
  (:import [org.bukkit Bukkit ChatColor]
           [org.bukkit.entity EntityType])
  (:gen-class :name soul_counter.core
              :extends org.bukkit.plugin.java.JavaPlugin
              :implements [org.bukkit.event.Listener]
              :methods [[^{org.bukkit.event.EventHandler true}
                         onDeath [org.bukkit.event.entity.EntityDeathEvent] void]]))

(def logger (atom nil))

(def all-player-data (ref {}))

(defrecord Data [type count msg])

(defn log-info [msg]
  (.info @logger msg))
(defn log-config [msg]
  (.config @logger msg))
(defn log-waring [msg]
  (.waring @logger msg))
(defn log-fine [msg]
  (.fine @logger msg))
(defn log-finer [msg]
  (.finer @logger msg))
(defn log-finest [msg]
  (.finest @logger msg))
(defn log-severe [msg]
  (.severe @logger msg))
(defn log-throwing [source-class source-method thrown]
  (.throwing @logger source-class source-method thrown))

(defn -onEnable [this]
  (reset! logger (.getLogger this))
  (-> this
      .getServer
      .getPluginManager
      (.registerEvents this this)))

(defn fmt [f num]
  (string/replace f "%NUM%" (str num)))

(defn get-entity-by-name [name]
  (try (EntityType/valueOf name) (catch Exception _ nil)))

(defn -onCommand [this sender cmd label args]
  (case (.getName cmd)
    "startcount" (if (>= (count args) 3)
                   (let [player-name (first args)]
                     (if-let [player (Bukkit/getPlayer player-name)]
                       (if-let [type (if (= (second args) "ALL")
                                       "ALL"
                                       (get-entity-by-name (second args)))]
                         (let [msg (string/join \space (nthnext args 2))]
                           (.sendMessage player (fmt msg 0))
                           (dosync (alter all-player-data assoc player-name (Data. type 0 msg)))
                           true)
                         (do (.sendMessage sender "无法找到目标生物") true))
                       (do (.sendMessage sender "无法找到目标玩家") true)))
                   false)
    "stopcount" (if-let [player-name (first args)]
                  (do (dosync (alter all-player-data dissoc player-name))
                      true)
                  false)
    false))

(defn -onDeath [this event]
  (let [entity (.getEntity event)
        entity-type (.getType entity)]
    (when-let [killer (.getKiller entity)]
      (let [player-name (.getDisplayName killer)
            data (get @all-player-data player-name)]
        ; 当已经开始记录这个玩家，并且玩家杀死的指定类型的怪物时
        (when (and data (or (= (get data :type) "ALL") (= (get data :type) entity-type)))
          (let [data (update data :count inc)]
            ;(prn (.getCustomName entity))
            (dosync (alter all-player-data assoc player-name data))
            (.sendMessage killer (fmt (get data :msg) (get data :count)))))))))
