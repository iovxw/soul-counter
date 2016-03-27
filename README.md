# soul-counter

Minecraft Bukkit 击杀计数器插件

## Usage

首先安装前置插件: <https://github.com/iovxw/cljminecraft>

然后把本插件放入插件文件夹内

两条命令:

1. `/startcount [player] [ALL|name] [msg]`
   - 权限: **soulcounter.startcount**
   - 例子: `/startcount iovxw ZOMBIE 僵尸击杀数量: %NUM%`
   - 开始玩家 iovxw 的计数, msg 中的 `%NUM%` 将会被替换为当前击杀数量
   - 想要统计全部生物的击杀请将例子中的 `ZOMBIE` 替换为 `ALL`
2. `/stopcount [player]`
   - 权限: **soulcounter.stopcount**
   - 例子: `/stopcount iovxw`
   - 停止玩家 iovxw 的计数

## License

Copyright (c) 2015 iovxw

The MIT License (MIT)
