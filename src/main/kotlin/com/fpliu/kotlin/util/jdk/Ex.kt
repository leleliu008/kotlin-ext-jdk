package com.fpliu.kotlin.util.jdk

import java.io.*
import java.net.URI
import java.net.URISyntaxException
import java.text.DecimalFormat
import java.util.*
import java.util.regex.Pattern
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

//https://stackoverflow.com/questions/36308666/check-if-all-bits-in-bitset-are-set-to-true
fun BitSet.isFull() = this.length() == this.nextClearBit(0)

fun isEmptyString(str: String?): Boolean = str == null || str.length == 0

/**
 * 判断str与正则表达式regex是否比配
 */
fun matches(regex: String, str: String?): Boolean {
    if (isEmptyString(str)) return false
    return Pattern.compile(regex).matcher(str).matches()
}

/**
 * 判断是否是手机号，规则是开头是1，并且是11位
 */
fun String.isPhoneNumber(): Boolean {
    if (length != 11) return false
    return matches("^[1][0-9]{10}$", this)
}

fun String.isSignedNumber(): Boolean {
    if (length == 0) return false
    return matches("^(\\+|\\-)?\\d+(\\.\\d+)?$", this)
}

fun String.isUnsignedNumber(): Boolean {
    if (length == 0) return false
    return matches("^\\d+(\\.\\d+)?$", this)
}

/**
 * 判断是否是浮点数
 * scale是小数的位数
 */
fun String.isFloatNumber(scale: Int): Boolean {
    if (length == 0) return false
    return matches("^\\d+(\\.\\d{$scale})$", this)
}

/**
 * 判断是否是正整数
 */
fun String.isPositiveIntegerNumber(): Boolean {
    if (length == 0) return false
    return matches("^\\+?\\d+$", this)
}

/**
 * 判断是否是v4类型的IP
 */
fun String.isIPv4(): Boolean {
    val regex = ("^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
            + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
            + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
            + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$")
    return matches(regex, this)
}

/**
 * 判断一个字符串是否包含emoji字符
 * Emoji字符在Unicode第一平面内，它的一个字符占用的空间超过了Java的一个字符占用的空间，所以，
 * 把一个Emoji字符拆分成两个Java字符，而拆分成的这两个字符与其他已经编码的Java字符的codePoint不会有相同的情况，
 * 这是Unicode当初在分配codePoint的时候故意这么设计的，虽然字符集与字符编码方案没有必然关系，但是当初分配codePoint是经过充分的考虑的，
 * 不是随意的。也就是说，Unicode第零平面内有65535个codePoint，但是，并不是这些所有的codePoint都被分配上了字符，
 * 有些codePoint是没有对应实际字符的，比如：
 *     区间名称          十六进制表示         十进制表示         个数
 * High Surrogates   0xD800..0xDBFF     55296 ~ 56319     1024个
 * Low Surrogates    0xDC00..0xDFFF     56320 ~ 57343     1024个
 * 因为是判断是否包含有Emoji，实际上，只要遍历此字符串，看看他们的codePoint是不是处于这个区间，如果处于这个区间，说明一定是包含有
 * 第一平面内的字符
 * 这里并不是精确的判断是否包含Emoji，因为第一平面内的字符不仅仅是Emoji，但实际上通常不关心这个问题，我们就认为他们都是Emoji即可
 */
fun String.containEmoji(): Boolean {
    if (length < 4) return false
    //https://kotlinlang.org/docs/reference/basic-types.html#strings
    for (char in this) {
        if (char.isSurrogate()) {
            return true
        }
    }
    return false
}

/**
 * 判定是否是汉字
 */
fun Char.isChinese(): Boolean {
    val ub = Character.UnicodeBlock.of(this)
    return (ub === Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
            || ub === Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
            || ub === Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
            || ub === Character.UnicodeBlock.GENERAL_PUNCTUATION
            || ub === Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
            || ub === Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS)
}

/**
 * 检测String是否全是中文字符
 */
fun String.isAllChinese(): Boolean {
    if (length < 2) return false
    //https://kotlinlang.org/docs/reference/basic-types.html#strings
    for (char in this) {
        if (!char.isChinese()) {
            return false
        }
    }
    return true
}

/**
 * 检测是否包含中文字符
 */
fun String.containChinese(): Boolean {
    if (length < 2) return false
    //https://kotlinlang.org/docs/reference/basic-types.html#strings
    for (char in this) {
        if (char.isChinese()) {
            return true
        }
    }
    return false
}

//获取Unicode字符的个数，不是字符串长度，Java中的字符串长度与我们普通人认知的字符个数不是一回事
//与MySQL的CHAR_LENGTH()函数在utf8mb4编码下的计算结果一致
//https://stackoverflow.com/questions/1734334/mysql-length-vs-char-length
//这是人眼实际看到的字符的个数，对人类友好
//Unicode字符个数与Java String.codePointCount() 方法不同的地方在于 Emoji编码的处理上
//U+200D是Emoji编码中的多个Emoji组合成一个Emoji的连接字符
fun String.getUnicodeCharacterCount(): Int {
    if (length == 0) return 0
    var count = 0
    var i = 0
    while (true) {
        if (i >= length) {
            break
        }

        val char = this[i]

        //紧挨着U+200D的后面两个Char跳过，不用算了
        if (char.toInt() == 0x200D) {
            i += 3
            continue
        }

        //紧挨着HighSurrogate的一定是LowSurrogate，跳过它
        if (char.isHighSurrogate()) {
            count++
            i += 2
            continue
        }

        count++
        i++
    }
    return count
}

//返回最多maxUnicodeCount个字符的子串
fun String.subStringMaxUnicodeCount(maxUnicodeCount: Int): String {
    if (length < 2) return this

    var count = 0
    var i = 0
    while (true) {
        if (i >= length || count >= maxUnicodeCount) {
            break
        }

        val char = this[i]

        //紧挨着U+200D的后面两个Char跳过，不用算了
        if (char.toInt() == 0x200D) {
            i += 3
            continue
        }

        //紧挨着HighSurrogate的一定是LowSurrogate，跳过它
        if (char.isHighSurrogate()) {
            count++
            i += 2
            continue
        }

        count++
        i++
    }

    return this.substring(0, i)
}

@Throws(IllegalArgumentException::class)
fun Number.toFixed2(): String {
    val value = DecimalFormat("0.00").format(this) ?: ""
    return if (value == "-0.00") "0.00" else value
}

//把16进制字符转换成10进制表示的数字
fun Char.hex2dec(): Int = when (this) {
    in '0'..'9' -> this - '0'
    in 'a'..'f' -> this - 'a' + 10
    in 'A'..'F' -> this - 'A' + 10
    else -> -1
}

const val BASE_16_CHARACTER_UPPER_TABLE = "0123456789ABCDEF"
const val BASE_16_CHARACTER_LOWER_TABLE = "0123456789abcdef"

fun ByteArray.base16Encode(upperCase: Boolean = true): String {
    if (size == 0) return ""

    val result = StringBuilder(size shl 1)
    for (byte in this) {
        val intValue = byte.toInt()
        //向右移动4bit，获得高4bit
        val highByte = intValue shr 4 and 0x0F
        //与0x0f做位与运算，获得低4bit
        val lowByte = intValue and 0x0F
        if (upperCase) {
            result.append(BASE_16_CHARACTER_UPPER_TABLE[highByte])
            result.append(BASE_16_CHARACTER_UPPER_TABLE[lowByte])
        } else {
            result.append(BASE_16_CHARACTER_LOWER_TABLE[highByte])
            result.append(BASE_16_CHARACTER_LOWER_TABLE[lowByte])
        }
    }
    return result.toString()
}

fun ByteArray.base16Encode2(upperCase: Boolean = true): String {
    if (size == 0) return ""
    val result = StringBuilder(size shl 1)
    for (byte in this) { // 使用String的format方法进行转换
        result.append(String.format(if (upperCase) "%02X" else "%02x", byte.toInt() and 0xFF))
    }
    return result.toString()
}

fun String.base16Decode(): ByteArray {
    val halfInputLength = length shr 1
    val output = ByteArray(halfInputLength)
    for (i in 0 until halfInputLength) {
        //16进制数字转换为10进制数字的过程
        val j = i shl 1
        output[i] = ((this[j].hex2dec() shl 4) + this[j + 1].hex2dec()).toByte()
    }
    return output
}

fun ByteArray.urlEncode(upperCase: Boolean = true): String {
    if (size == 0) return ""

    val result = StringBuilder()
    for (byte in this) {
        val codePoint = byte.toInt()
        when (codePoint) {
            //man ascii看ASCII编码对应表
            //'0'~'9'    'A'~'Z'    'a'~'z'
            in 48..57, in 65..90, in 97..122 -> result.append(byte.toChar())
            //-_.*
            45, 95, 46, 42 -> result.append(byte.toChar())
            //空格
//            32 -> result.append('+')
            else -> {
                //向右移动4bit，获得高4bit
                val highByte = codePoint shr 4 and 0x0F
                //与0x0f做位与运算，获得低4bit
                val lowByte = codePoint and 0x0F
                result.append('%')
                if (upperCase) {
                    result.append(BASE_16_CHARACTER_UPPER_TABLE[highByte])
                    result.append(BASE_16_CHARACTER_UPPER_TABLE[lowByte])
                } else {
                    result.append(BASE_16_CHARACTER_LOWER_TABLE[highByte])
                    result.append(BASE_16_CHARACTER_LOWER_TABLE[lowByte])
                }
            }
        }
    }
    return result.toString()
}

fun String.urlDecode(): ByteArray {
    if (length == 0) return ByteArray(0)

    val output = ByteArray(length)
    var outputLength = 0
    var i = 0
    while (i < length) {
        val c = this[i]
        if (c == '%') {
            val x = this[++i]
            val y = this[++i]
            //16进制数字转换为10进制数字的过程
            output[outputLength++] = ((x.hex2dec() shl 4) + y.hex2dec()).toByte()
        } else {
            output[outputLength++] = c.toByte()
        }
        i++
    }
    val outputCopy = ByteArray(outputLength)
    System.arraycopy(output, 0, outputCopy, 0, outputLength)
    return outputCopy
}

//模拟mkdir -p 命令
fun File.mkdirP(): Boolean = if (exists() && isDirectory) true else mkdirs()

//模拟touch命令
@Throws(IOException::class)
fun File.touch(): Boolean {
    if (exists()) {
        if (isFile) {
            return true
        } else {
            throw IOException("$absolutePath already exists, but not file")
        }
    }
    if (parentFile.mkdirP()) {
        return createNewFile()
    }
    return false
}

//模拟exec命令
fun exec(command: String): String? {
    var input: BufferedReader? = null
    try {
        val p = Runtime.getRuntime().exec(command)
        input = BufferedReader(InputStreamReader(p.inputStream), 1024)
        return input.readLine()
    } catch (ex: IOException) {
        ex.printStackTrace()
        return null
    } finally {
        if (input != null) {
            try {
                input.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}

//模拟unzip命令
//unzip(zipFile, destDir) { inputStream: InputStream, desFile: File ->
//    val bufferedSink = Okio.buffer(Okio.sink(desFile))
//    bufferedSink.writeAll(Okio.source(inputStream))
//    bufferedSink.close()
//}
//成功的话，返回destDir，方便在Observable中传递
@Throws(IOException::class)
fun unzip(inFile: File, destDir: File, fileWriter: (InputStream, File) -> Unit): File {
    destDir.run { if (!exists()) mkdirs() }

    val zipFile = ZipFile(inFile)
    val entries = zipFile.entries()
    while (entries.hasMoreElements()) {
        val zipEntry = entries.nextElement() as ZipEntry

        val desFile = File(destDir, zipEntry.name)

        //如果是文件夹类型，创建文件夹即可
        if (zipEntry.isDirectory) {
            desFile.apply {
                if (exists()) {
                    if (isFile) {
                        delete()
                        mkdir()
                    }
                } else {
                    mkdir()
                }
            }
            continue
        }

        //文件类型
        desFile.run { if (!exists()) createNewFile() }

        fileWriter.invoke(zipFile.getInputStream(zipEntry), desFile)
    }
    return destDir
}

//&符号可能是被HTML转义过成为$amp;，这要不要还原回去？
@Throws(URISyntaxException::class)
fun String.appendQueryParams(params: String): String {
    if (this == "") return ""
    if (params == "") return this
    return "${this}${if (isEmptyString(URI(this).query)) "?" else "&"}$params"
}

fun Long.toTimeFormatStr(): String {
    val totalSeconds = this / 1000
    val minute = totalSeconds / 60
    val seconds = totalSeconds % 60
    val formatStr = StringBuilder()
    if (minute < 10) {
        formatStr.append("0")
    }
    formatStr.append(minute)
    formatStr.append(":")
    if (seconds < 10) {
        formatStr.append("0")
    }
    formatStr.append(seconds)
    return formatStr.toString()
}

//注意：此判断非常不可靠，在release编译的时候，应该将此方法替换为永久返回true
//存在此函数的理由是在调试阶段
fun isAndroidRuntime(): Boolean {
    val runtimeName = System.getProperty("java.runtime.name")
    return runtimeName !== "Java(TM) SE Runtime Environment" && runtimeName !== "OpenJDK Runtime Environment"
}
