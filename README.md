# analysisXml
一个帮助获取android项目中string文件中文数据，和帮助自动补全多语言string数据的小工具

主要有三个功能

1 补全，在父xml中添加新的string标签，运行之后，会在对应的子xml中相应的位置添加

2 获取中文，获取子xml中有getChinese标签的xml中所有有中文的string，并写入excel表

3 自动替换翻译，将所有翻译数据放在一个excel表里面，会根据子xml里面写有的needTranslate对应的语言，自动替换excel表中对应的数据

使用步骤：

第一步，先修改pathUitl的路径，比如查找后中文的数据存放地址，还有翻译excel文档的存放地址等，改为自己本地的路径，

第二步，修改moduleSetting中的module.xml，改为自己项目地址strings.xml文件存放地址，并且根据需要添加配置属性，如getchinese和needtranslate

第三步，根据需要运行相关java文件就行，
