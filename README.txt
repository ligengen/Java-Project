双人对战贪吃蛇

运行方法：
	首先双击server.jar, 可以默认端口号，也可以更换端口号，点击确定
	再双击client.jar, ip地址默认，如果server启动时换过端口号，则在此处输入一样的端口号，点击确定
	再双击client.jar启动第二个客户端，注意！！！有可能因为已经启动了一个，再次点击提示已经启动的那个，启动不了第二个，解决方案：将client.jar复制得到它的副本，双击副本即可解决，ip与端口号同上。


使用方法：
	两个客户端运行起来之后，会看到第一个运行起来的客户端没有蛇，第二个运行起来的客户端有两条蛇，依次作为区分
	第一个客户端用上下左右控制紫蛇，第二个客户端用wasd控制绿蛇。
	

开始游戏方法：
	点击某一个客户端的窗口，保证该客户端窗口在选中状态（可以适当拖动窗体保证焦点落在该窗体上）。之后将鼠标移动进入电源状按钮内部，可以发现鼠标变成了手指状，此时单击按钮，两个窗体弹出5秒后游戏开始，等待5秒后对话框自动关闭，游戏开始。


暂停游戏方法：“空格键”
	首先保证焦点在某一个客户端的窗体上，之后按“空格”键，即可暂停，此时可见提示窗体，关闭窗体


继续游戏方法：“F1键”
	首先保证焦点在某一个客户端的窗体上，之后按“F1”键，即可重新启动游戏


调节蛇速度：slider滑动控制蛇速，越往右蛇的速度越慢，越往左越快。


注意事项：
	1、server和client严格配对，如果某次client连接之后直接关掉，紧接的client一定是和关掉的那个配对，所以要避免运行两个不配对的client。具体操作就是严格按照要求，先成功运行一个client之后再成功运行另一个client。如果出现误操作使得不能配对成功，建议关掉server重来或者运行第三个client和第四个client。
	2、启动第二个client的时候如果有问题，建议复制client.jar得到client的副本.jar，双击client的副本.jar即可。
	3、如果开始按钮点击没有反应，建议拖动窗体后再次点击，有可能是焦点的问题
	4、切换操纵蛇时需要点击另一个窗体的左侧游戏界面，使其获得焦点，才可以按键有效



