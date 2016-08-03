# Jgraph
[![License](https://img.shields.io/badge/license-Apache%202-green.svg?style=flat-square)](https://www.apache.org/licenses/LICENSE-2.0)
![](https://img.shields.io/badge/Jgraph-download-brightgreen.svg?style=flat-square)(http://fir.im/y57x)
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
![](data:image/png;base64,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)(http://pkg3.fir.im/c057abbf2f19de03cd602953b1a691379643faa0.apk?filename=app-debug.apk_1.0.apk)

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
