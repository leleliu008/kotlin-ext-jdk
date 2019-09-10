# kotlin-ext-jdk

常用的简化`jdk`的`kotlin`实现
<br>
## 如何引用
`gradle kotlin dsl`:
```
repositories {
    //https://maven.aliyun.com/mvn/view
    maven { url = uri("https://maven.aliyun.com/repository/jcenter") }
}

dependencies {
    implementation("com.fpliu:kotlin-ext-jdk:1.0.0")
}
```
其他方式请看[jcenter](https://bintray.com/fpliu/newton/kotlin-ext-jdk)

## 文档
##### fun BitSet.isFull()
判断`BitSet`的所有位是否都为`1`
<br><br>

##### fun isEmptyString(str: String?): Boolean
判断`str`是否是空
<br><br>

##### fun matches(regex: String, str: String?): Boolean
判断`str`与正则表达式`regex`是否比配
<br><br>

##### fun String.isPhoneNumber(): Boolean
判断`String`是否是手机号，规则是开头是`1`，并且是`11`位
<br><br>

##### fun String.isFloatNumber(scale: Int): Boolean
判断`String`是否是浮点数，`scale`是你希望小数点后的位数
<br><br>

##### fun String.isPositiveIntegerNumber(): Boolean
判断`String`是否是正整数
<br><br>

##### fun String.isIPv4(): Boolean
判断`String`是否是`IPv4`
<br><br>

##### fun String.containEmoji(): Boolean
判断`String`中是否包含有`Emoji`字符
<br><br>

##### fun String.isAllChinese(): Boolean
判断`String`是否全部由`中文`组成
<br><br>

##### fun String.containChinese(): Boolean
判断`String`中是否包含有`中文`
<br><br>

##### fun String.getUnicodeCharacterCount(): Int
获得一个字符串中的`Unicode字符的个数`。

`Unicode字符的个数`不是`Java`中的字符串长度，`Java`中的字符串长度与`普通人`认知的字符个数不是一回事。

`Unicode字符的个数`是人眼实际看到的字符的个数，对人类友好，经常在用户提示中告诉最多输入多少个字符的时候用到，在关系型数据库中约束一个`TEXT`类型的字段的时候也要用到。

`Unicode字符个数`与`Java`的`String.codePointCount()`方法不同的地方在于`Emoji`编码的处理上。

此方法与`MySQL`的`CHAR_LENGTH()`函数在`utf8mb4`编码下的计算结果一致
https://stackoverflow.com/questions/1734334/mysql-length-vs-char-length

<br><br>

##### fun String.subStringMaxUnicodeCount(maxUnicodeCount: Int): String
截取一个字符串的前面`maxUnicodeCount`个`Unicode`字符的子串。
<br><br>

##### fun Number.toFixed2(): String
将数字格式化为保留`2`位小数，在显示`金额`的时候经常用。
<br><br>

##### fun Char.hex2dec(): Int
把`表示16进制的字符`转成`10进制数`，用于查表。

|Char|hex2dec()|toInt()
|-|-|-|
|'0'|0|48|
|'1'|1|49|
|'2'|2|50|
|'3'|3|51|
|'4'|4|52|
|'5'|5|53|
|'6'|6|54|
|'7'|7|55|
|'8'|8|56|
|'9'|9|57|
|'A'或'a'|10|65|
|'B'或'b'|11|66|
|'C'或'c'|12|67|
|'D'或'd'|13|68|
|'E'或'e'|14|69|
|'F'或'f'|15|70|
<br>

##### const val BASE_16_CHARACTER_UPPER_TABLE = "0123456789ABCDEF"
`表示16进制的大写字符`表
<br><br>

##### const val BASE_16_CHARACTER_LOWER_TABLE = "0123456789abcdef"
`表示16进制的小写字符`表
<br><br>

##### fun ByteArray.base16Encode(): String
[base16](http://blog.fpliu.com/it/data/text/encoding/Base16)编码
<br><br>

##### fun ByteArray.base16Encode2(): String
[base16](http://blog.fpliu.com/it/data/text/encoding/Base16)编码的第二种实现
<br><br>

##### fun String.base16Decode(): ByteArray
[base16](http://blog.fpliu.com/it/data/text/encoding/Base16)解码
<br><br>

##### fun ByteArray.urlEncode(): String
[URL](http://blog.fpliu.com/it/data/text/encoding/URL)编码
<br><br>

##### fun String.urlDecode(): ByteArray
[URL](http://blog.fpliu.com/it/data/text/encoding/URL)解码
<br><br>

##### fun File.mkdirP(): Boolean
模拟`mkdir -p`命令
<br><br>

##### fun File.touch(): Boolean
模拟`touch`命令
<br><br>

##### fun unzip(inFile: File, destDir: File, fileWriter: (InputStream, File) -> Unit): File
解压`zip`文件

`inFile`是要解压的`zip`文件

`destDir`是要解压到的目录

`fileWriter`留给用户自己实现。

使用`Okio`实现：
```
unzip(zipFile, destDir) { inputStream: InputStream, desFile: File ->
    val bufferedSink = Okio.buffer(Okio.sink(desFile))
    bufferedSink.writeAll(Okio.source(inputStream))
    bufferedSink.close()
}
```
使用普通的`Kotlin`实现：
```
unzip(zipFile, destDir) { inputStream: InputStream, file: File ->
    var outputStream: FileOutputStream? = null
    var bufferedReader: BufferedInputStream? = null
    try {
        outputStream = FileOutputStream(file)
        bufferedReader = BufferedInputStream(inputStream)
        val buffer = ByteArray(2048)
        var readSize: Int
        while (true) {
            readSize = bufferedReader.read(buffer, 0, buffer.size)
            if (readSize == -1) break
            outputStream.write(buffer, 0, readSize)
        }
    } finally {
        if (bufferedReader != null) {
            try {
                bufferedReader.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (outputStream != null) {
            try {
                outputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
```
<br><br>

##### fun String.appendQueryParams(params: String): String
往一个`URL`上继续添加一些参数，添加的部分可能需要自己做`URL编码`，要不要做`URL编码`，取决于你使用的`HTTP库`

