package com.fpliu.kotlin.util.jdk

import org.junit.Test
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class Test {

    @Test
    fun test_isEmptyString() {
        assert(isEmptyString(""))
        assert(isEmptyString(null))
        assert(!isEmptyString("d"))
    }

    @Test
    fun test_isPhoneNumber() {
        assert("12345678901".isPhoneNumber())
        assert(!"1234567890".isPhoneNumber())
        assert(!"123456789012".isPhoneNumber())
        assert(!"".isPhoneNumber())
        assert(!"abcdefghkim".isPhoneNumber())
        assert(!"+1234567890".isPhoneNumber())
    }

    @Test
    fun test_isSignedNumber() {
        assert("0".isSignedNumber())
        assert("+0".isSignedNumber())
        assert("-0".isSignedNumber())
        assert("0.1".isSignedNumber())
        assert("+0.1".isSignedNumber())
        assert("-0.1".isSignedNumber())
        assert(!"+0.1.0".isSignedNumber())
    }

    @Test
    fun test_isUnsignedNumber() {
        assert("0".isUnsignedNumber())
        assert(!"+0".isUnsignedNumber())
        assert(!"-0".isUnsignedNumber())
        assert("0.1".isUnsignedNumber())
        assert(!"+0.1".isUnsignedNumber())
        assert(!"-0.1".isUnsignedNumber())
        assert(!"+0.1.0".isUnsignedNumber())
    }

    @Test
    fun test_isFloatNumber() {
        assert("0.5".isFloatNumber(1))
        assert("0.50".isFloatNumber(2))
        assert("0.507".isFloatNumber(3))
        assert(!"0".isFloatNumber(1))
        assert(!"1".isFloatNumber(1))
        assert(!"ss".isFloatNumber(2))
        assert(!"中国".isFloatNumber(3))
        assert(!"+1234567890".isFloatNumber(2))
    }

    @Test
    fun test_isPositiveIntegerNumber() {
        assert("0".isPositiveIntegerNumber())
        assert("1".isPositiveIntegerNumber())
        assert("+1".isPositiveIntegerNumber())
        assert("10".isPositiveIntegerNumber())
        assert(!"-1".isPositiveIntegerNumber())
        assert(!"0.5".isPositiveIntegerNumber())
        assert(!"1.1".isPositiveIntegerNumber())
    }

    @Test
    fun test_isIPv4() {
        assert("192.168.0.0".isIPv4())
        assert(!"1.d.2.5".isIPv4())
        assert(!"haha".isIPv4())
        assert(!"10".isIPv4())
        assert(!"-1".isIPv4())
        assert(!"0.5".isIPv4())
        assert(!"1.1".isIPv4())
    }

    @Test
    fun test_containEmoji() {
        assert("😀😀😀😂😂".containEmoji())
        assert("1.d.2.5😎".containEmoji())
        assert("🔥🍉haha".containEmoji())
        assert("中国🍥🍭🎂🌶🌽你好".containEmoji())
        assert(!"中国".containEmoji())
        assert(!"0.5".containEmoji())
        assert(!"abcdefghikklmn".containEmoji())
        assert(!"，。,.=_".containEmoji())
    }

    @Test
    fun test_isAllChinese() {
        assert("給點陽光我就腐爛".isAllChinese())
        assert("中国".isAllChinese())
        assert("，。".isAllChinese())
        assert(!",.=_".isAllChinese())
        assert(!"中国🍥🍭🎂🌶🌽你好".isAllChinese())
        assert(!"😀😀😀😂😂".isAllChinese())
        assert(!"こんにちは".isAllChinese())
        assert(!"こんにちは💯".isAllChinese())
        assert(!"안녕하세요".isAllChinese())
        assert(!"안녕하세요.".isAllChinese())
        assert(!"ゞ給點陽光我就腐爛ゞ".isAllChinese())
        assert(!"1.d.2.5😎".isAllChinese())
        assert(!"🔥🍉haha".isAllChinese())
        assert(!"0.5".isAllChinese())
        assert(!"abcdefghikklmn".isAllChinese())
    }

    @Test
    fun test_containChinese() {
        assert("給點陽光我就腐爛".containChinese())
        assert("ゞ給點陽光我就腐爛ゞ".containChinese())
        assert("中国".containChinese())
        assert("welcome to 中国".containChinese())
        assert("中国🍥🍭🎂🌶🌽你好".containChinese())
        assert("，。".containChinese())
        assert(",.=_，。".containChinese())

        assert(!",.=_".containChinese())
        assert(!"😀😀😀😂😂".containChinese())
        assert(!"こんにちは".containChinese())
        assert(!"こんにちは💯".containChinese())
        assert(!"안녕하세요".containChinese())
        assert(!"안녕하세요.".containChinese())
        assert(!"1.d.2.5😎".containChinese())
        assert(!"🔥🍉haha".containChinese())
        assert(!"0.5".containChinese())
        assert(!"abcdefghikklmn".containChinese())
    }

    @Test
    fun test_getUnicodeCharacterCount() {
        assert("給點陽光我就腐爛".getUnicodeCharacterCount() == 8)
        assert("😀😀😀😂😂".getUnicodeCharacterCount() == 5)
        assert("中国🍥🍭🎂🌶🌽你好".getUnicodeCharacterCount() == 9)
        assert("🔥🍉haha".getUnicodeCharacterCount() == 6)
        assert("中国".getUnicodeCharacterCount() == 2)
        assert("0.5".getUnicodeCharacterCount() == 3)
        assert("こんにちは💯".getUnicodeCharacterCount() == 6)
        //这里是为啥呢？因为这是个组合型的Emoji
        //https://emojipedia.org/emoji/👨‍👩‍👦/
        assert("👨‍👩‍👦".getUnicodeCharacterCount() == 1)
        assert("👪".getUnicodeCharacterCount() == 1)
    }

    @Test
    fun test_subStringMaxUnicodeCount() {
        assert("給點陽光我就腐爛".subStringMaxUnicodeCount(2) == "給點")
        assert("😀😀😀😂😂".subStringMaxUnicodeCount(3) == "😀😀😀")
        assert("🔥🍉haha".subStringMaxUnicodeCount(1) == "🔥")
        assert("welcome to 中国".subStringMaxUnicodeCount(12) == "welcome to 中")
        assert("0.5".subStringMaxUnicodeCount(2) == "0.")
        assert("こんにちは💯".subStringMaxUnicodeCount(5) == "こんにちは")
        assert("こんにちは💯".subStringMaxUnicodeCount(32) == "こんにちは💯")
    }

    @Test
    fun test_toFixed2() {
        assert(0.toFixed2() == "0.00")
        assert(2.toFixed2() == "2.00")
        assert(0.2.toFixed2() == "0.20")
        //四舍五入
        assert(0.274.toFixed2() == "0.27")
        assert(0.278.toFixed2() == "0.28")

        assert((-100000).toFixed2() == "-100000.00")
    }

    @Test
    fun test_hex2dec() {
        assert('0'.hex2dec() == 0)
        assert('1'.hex2dec() == 1)
        assert('2'.hex2dec() == 2)
        assert('3'.hex2dec() == 3)
        assert('4'.hex2dec() == 4)
        assert('5'.hex2dec() == 5)
        assert('6'.hex2dec() == 6)
        assert('7'.hex2dec() == 7)
        assert('8'.hex2dec() == 8)
        assert('9'.hex2dec() == 9)
        assert('a'.hex2dec() == 10)
        assert('A'.hex2dec() == 10)
        assert('b'.hex2dec() == 11)
        assert('B'.hex2dec() == 11)
        assert('c'.hex2dec() == 12)
        assert('C'.hex2dec() == 12)
        assert('d'.hex2dec() == 13)
        assert('D'.hex2dec() == 13)
        assert('e'.hex2dec() == 14)
        assert('E'.hex2dec() == 14)
        assert('f'.hex2dec() == 15)
        assert('F'.hex2dec() == 15)
        assert('g'.hex2dec() == -1)
        assert('G'.hex2dec() == -1)
    }

    //验证 https://www.qqxiuzi.cn/bianma/base.php?type=16
    @Test
    fun test_base16Encode() {
        assert("0".toByteArray().base16Encode() == "30")
        assert("0.5".toByteArray().base16Encode() == "302E35")
        assert("給點陽光我就腐爛".toByteArray().base16Encode() == "E7B5A6E9BB9EE999BDE58589E68891E5B0B1E88590E7889B")
        assert("😀😀😀😂😂".toByteArray().base16Encode() == "F09F9880F09F9880F09F9880F09F9882F09F9882")
        assert("🔥🍉haha".toByteArray().base16Encode() == "F09F94A5F09F8D8968616861")
        assert("welcome to 中国".toByteArray().base16Encode() == "77656C636F6D6520746F20E4B8ADE59BBD")
        assert("こんにちは💯".toByteArray().base16Encode() == "E38193E38293E381ABE381A1E381AFF09F92AF")
    }

    //验证 https://www.qqxiuzi.cn/bianma/base.php?type=16
    @Test
    fun test_base16Encode2() {
        assert("0".toByteArray().base16Encode2() == "30")
        assert("0.5".toByteArray().base16Encode2() == "302E35")
        assert("給點陽光我就腐爛".toByteArray().base16Encode2() == "E7B5A6E9BB9EE999BDE58589E68891E5B0B1E88590E7889B")
        assert("😀😀😀😂😂".toByteArray().base16Encode2() == "F09F9880F09F9880F09F9880F09F9882F09F9882")
        assert("🔥🍉haha".toByteArray().base16Encode2() == "F09F94A5F09F8D8968616861")
        assert("welcome to 中国".toByteArray().base16Encode2() == "77656C636F6D6520746F20E4B8ADE59BBD")
        assert("こんにちは💯".toByteArray().base16Encode2() == "E38193E38293E381ABE381A1E381AFF09F92AF")
    }

    @Test
    fun test_base16Decode() {
        assert(String("30".base16Decode()) == "0")
        assert(String("302E35".base16Decode()) == "0.5")
        assert(String("E7B5A6E9BB9EE999BDE58589E68891E5B0B1E88590E7889B".base16Decode()) == "給點陽光我就腐爛")
        assert(String("F09F9880F09F9880F09F9880F09F9882F09F9882".base16Decode()) == "😀😀😀😂😂")
        assert(String("F09F94A5F09F8D8968616861".base16Decode()) == "🔥🍉haha")
        assert(String("77656C636F6D6520746F20E4B8ADE59BBD".base16Decode()) == "welcome to 中国")
        assert(String("E38193E38293E381ABE381A1E381AFF09F92AF".base16Decode()) == "こんにちは💯")
    }

    //把字符串输入浏览器地址栏中，然后再复制就是URL编码过的
    @Test
    fun test_urlEncode() {
        assert("华为方舟编译器".toByteArray().urlEncode() == "%E5%8D%8E%E4%B8%BA%E6%96%B9%E8%88%9F%E7%BC%96%E8%AF%91%E5%99%A8")
        assert("0.5".toByteArray().urlEncode() == "0.5")
        assert("給點陽光我就腐爛".toByteArray().urlEncode() == "%E7%B5%A6%E9%BB%9E%E9%99%BD%E5%85%89%E6%88%91%E5%B0%B1%E8%85%90%E7%88%9B")
        assert("😀😀😀😂😂".toByteArray().urlEncode() == "%F0%9F%98%80%F0%9F%98%80%F0%9F%98%80%F0%9F%98%82%F0%9F%98%82")
        assert("🔥🍉haha".toByteArray().urlEncode() == "%F0%9F%94%A5%F0%9F%8D%89haha")
        assert("welcome to 中国".toByteArray().urlEncode() == "welcome%20to%20%E4%B8%AD%E5%9B%BD")
        assert("こんにちは💯".toByteArray().urlEncode() == "%E3%81%93%E3%82%93%E3%81%AB%E3%81%A1%E3%81%AF%F0%9F%92%AF")
    }

    //验证 http://tool.chinaz.com/tools/urlencode.aspx
    @Test
    fun test_urlDecode() {
        assert(String("%E5%8D%8E%E4%B8%BA%E6%96%B9%E8%88%9F%E7%BC%96%E8%AF%91%E5%99%A8".urlDecode()) == "华为方舟编译器")
        assert(String("0.5".urlDecode()) == "0.5")
        assert(String("%E7%B5%A6%E9%BB%9E%E9%99%BD%E5%85%89%E6%88%91%E5%B0%B1%E8%85%90%E7%88%9B".urlDecode()) == "給點陽光我就腐爛")
        assert(String("%F0%9F%98%80%F0%9F%98%80%F0%9F%98%80%F0%9F%98%82%F0%9F%98%82".urlDecode()) == "😀😀😀😂😂")
        assert(String("%F0%9F%94%A5%F0%9F%8D%89haha".urlDecode()) == "🔥🍉haha")
        assert(String("welcome%20to%20%E4%B8%AD%E5%9B%BD".urlDecode()) == "welcome to 中国")
        assert(String("%E3%81%93%E3%82%93%E3%81%AB%E3%81%A1%E3%81%AF%F0%9F%92%AF".urlDecode()) == "こんにちは💯")
    }

    @Test
    fun test_mkdirP() {
        val file = File("${System.getProperty("user.home")}/ccc/ddd/eee/a.txt")
        assert(file.mkdirP())
        assert(file.exists())
        assert(file.isDirectory)
    }

    @Test
    fun test_touch() {
        val file = File("${System.getProperty("user.home")}/ccc/ddd/eee/b.txt")
        assert(file.touch())
        assert(file.exists())
        assert(file.isFile)
    }

    @Test
    fun test_exec() {
        assert(exec("pwd") == File("").absolutePath)
    }

    @Test
    fun test_unzip() {
        val currentDir = File("").absolutePath
        val zipFile = File("$currentDir/test.zip")
        val destDir = File("$currentDir/unzip")
        assert(unzip(zipFile, destDir) { inputStream: InputStream, file: File ->
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
        } == destDir)
    }

    @Test
    fun test_appendQueryParams() {
        assert("http://blog.fpliu.com".appendQueryParams("id=400") == "http://blog.fpliu.com?id=400")
        assert("http://blog.fpliu.com?x=10".appendQueryParams("id=400") == "http://blog.fpliu.com?x=10&id=400")
    }
}
