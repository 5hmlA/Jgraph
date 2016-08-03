# Jgraph
[![License](https://img.shields.io/badge/license-Apache%202-green.svg?style=flat-square)](https://www.apache.org/licenses/LICENSE-2.0)
![](https://img.shields.io/badge/Jgraph-download-brightgreen.svg?style=flat-square)
[![](https://jitpack.io/v/mychoices/Jgraph.svg)](https://jitpack.io/#mychoices/Jgraph)

![](https://raw.githubusercontent.com/mychoices/Jgraph/master/gif/2.gif)
![](https://raw.githubusercontent.com/mychoices/Jgraph/master/gif/4.gif)
![](https://raw.githubusercontent.com/mychoices/Jgraph/master/gif/6.gif)
![](https://raw.githubusercontent.com/mychoices/Jgraph/master/gif/13.gif)
![](https://raw.githubusercontent.com/mychoices/Jgraph/master/gif/15.gif)
![](https://raw.githubusercontent.com/mychoices/Jgraph/master/gif/22.gif)
![](https://raw.githubusercontent.com/mychoices/Jgraph/master/gif/020.gif)
![](https://raw.githubusercontent.com/mychoices/Jgraph/master/gif/021.gif)

2. [Gradle](https://github.com/mychoices/Jgraph/blob/master/README.md#gradle)
3. [Demo](https://github.com/mychoices/Jgraph/blob/master/README.md#demo)
4. [Use Guide](https://github.com/mychoices/Jgraph/blob/master/README.md#use-guide)
    1. [图表风格](https://github.com/mychoices/Jgraph/blob/master/README.md#graphstyle)
    2. [滚动](https://github.com/mychoices/Jgraph/blob/master/README.md#scrollable)
    2. [纵轴](https://github.com/mychoices/Jgraph/blob/master/README.md#纵轴)
    3. [柱-动画](https://github.com/mychoices/Jgraph/blob/master/README.md#barshowstyle)
    4. [柱-颜色](https://github.com/mychoices/Jgraph/blob/master/README.md#barcolor)
    4. [线-动画](https://github.com/mychoices/Jgraph/blob/master/README.md#lineshowstyle)
    4. [线-风格](https://github.com/mychoices/Jgraph/blob/master/README.md#linestyle)
    5. [线-断0](https://github.com/mychoices/Jgraph/blob/master/README.md#linemode)
    7. [线-颜色](https://github.com/mychoices/Jgraph/blob/master/README.md#linecolor)
    8. [选中](https://github.com/mychoices/Jgraph/blob/master/README.md#select)
    9. [切换数据](https://github.com/mychoices/Jgraph/blob/master/README.md#datachange)
5. [Versions](https://github.com/mychoices/Jgraph/blob/master/README.md#versions)
6. [Todo](https://github.com/mychoices/Jgraph/blob/master/README.md#todo)
7. [License](https://github.com/mychoices/Jgraph/blob/master/README.md#license)

#Gradle

	allprojects {
	        repositories {
			...
			maven { url "https://jitpack.io" }
		}
	}
	dependencies {
	    compile 'com.github.mychoices:Jgraph:16.07.30'
	}

#DEMO

demo下载地址

#Use Guide
---
###*自定义属性*
```
<attr name="graphstyle" format="enum">
    <enum name="bar" value="0"/>
    <enum name="line" value="1"/>
</attr>
<attr name="scrollable" format="boolean"/>  
<attr name="visiblenums" format="integer"/>
<attr name="showymsg" format="boolean"/>
<attr name="normolcolor" format="color"/>
<attr name="activationcolor" format="color"/>
<attr name="linestyle" format="enum">
    <!--折线-->
    <enum name="broken" value="0"/>
    <!--曲线-->
    <enum name="curve" value="1"/>
</attr>
<attr name="linemode" format="enum">
    <!--链接每一个点-->
    <enum name="everypoint" value="1"/>
    <!--跳过0的点-->
    <enum name="jump0" value="2"/>
    <!--跳过的0点用虚线链接-->
    <enum name="dash0" value="3"/>
</attr>
<attr name="linewidth" format="dimension"/>
<attr name="lineshowstyle" format="enum">
    <enum name="drawing" value="0"/>
    <enum name="section" value="1"/>
    <enum name="fromline" value="2"/>
    <enum name="fromcorner" value="3"/>
    <enum name="aswave" value="4"/>
</attr>
```

## GraphStyle

```
setGraphStyle(@GraphStyle int graphStyle) //柱状图 和 折线图
```
![](https://raw.githubusercontent.com/mychoices/Jgraph/master/gif/01.gif)
## Scrollable
```
setScrollAble(boolean )
```
![](https://raw.githubusercontent.com/mychoices/Jgraph/master/gif/02.gif)
## 纵轴

```
setYaxisValues(@NonNull String... showMsg)
setYaxisValues(int max, int showYnum)
setYaxisValues(int min, int max, int showYnum)
```
![](https://raw.githubusercontent.com/mychoices/Jgraph/master/gif/03.gif)

## BarShowStyle
```
setBarShowStyle(@BarShowStyle int barShowStyle)
/**
 * 水波 方式生长
 */
int BARSHOW_ASWAVE 
/**
 * 线条 一从直线慢慢变成折线/曲线
 */
int BARSHOW_FROMLINE 
/**
 * 柱形条 由某个往外扩散
 */
int BARSHOW_EXPAND 

/**
 * 一段一段显示
 */
int BARSHOW_SECTION 
```
![](https://raw.githubusercontent.com/mychoices/Jgraph/master/gif/04.gif)

## barcolor
```
setNormalColor(@ColorInt int normalColor)
setPaintShaderColors(@ColorInt int... colors)
```
![](https://raw.githubusercontent.com/mychoices/Jgraph/master/gif/05.gif)
## LineStyle
```
setLineStyle(@LineStyle int lineStyle)
/**
 * 折线
 */
int LINE_BROKEN = 0;
/**
 * 曲线
 */
int LINE_CURVE = 1;
```
![](https://raw.githubusercontent.com/mychoices/Jgraph/master/gif/06.gif)
## LineShowStyle
```
setLineShowStyle(@LineShowStyle int lineShowStyle)

/**
 * 线条从无到有 慢慢出现
 */
int LINESHOW_DRAWING 
/**
 * 线条 一段一段显示
 */
int LINESHOW_SECTION 
/**
 * 线条 一从直线慢慢变成折线/曲线
 */
int LINESHOW_FROMLINE 

/**
 * 从左上角 放大
 */
int LINESHOW_FROMCORNER 
/**
 * 水波 方式展开
 */
int LINESHOW_ASWAVE 
```
![](https://raw.githubusercontent.com/mychoices/Jgraph/master/gif/07.gif)
## LineMode
```
setLineMode(@LineMode int lineMode)
/**
 * 连接每一个点
 */
int LINE_EVERYPOINT 
/**
 * 跳过0  断开
 */
int LINE_JUMP0 

/**
 * 跳过0 用虚线链接
 */
int LINE_DASH_0 
```
![](https://raw.githubusercontent.com/mychoices/Jgraph/master/gif/08.gif)

## linecolor
```
setNormalColor(@ColorInt int normalColor)
setPaintShaderColors(@ColorInt int... colors)
setShaderAreaColors(@ColorInt int... colors)
```
![](https://raw.githubusercontent.com/mychoices/Jgraph/master/gif/09.gif)
## select
```
setSelected(int selected)
setSelectedMode(@SelectedMode int selectedMode)
```
![](https://raw.githubusercontent.com/mychoices/Jgraph/master/gif/010.gif)

# datachange
```
aniChangeData(List<Jchart> jchartList)
```
![](https://raw.githubusercontent.com/mychoices/Jgraph/master/gif/011.gif)

# Versions



# Todo
Todo

# License

    Copyright 2016 Yun

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
