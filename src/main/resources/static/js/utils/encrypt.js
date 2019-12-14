/* 声明一个数据加密模块 */
define(['lib/crypto-js'], function (CryptoJS) {

    return{
        //Hashing加密
        encryptWithHashing: function(message, type) {
            switch (type) {
                case "SHA256":
                    return CryptoJS.SHA256(message + type).toString(CryptoJS.enc.Base64);
                case "MD5":
                    return CryptoJS.MD5(message + type).toString(CryptoJS.enc.Base64);
                default:
                    return null;
            }
        },
        /* AES加解密 */
        //AES加密
        encryptWithAES: function(message, key, vi) {
            let encrypted = CryptoJS.AES.encrypt(CryptoJS.enc.Utf8.parse(message), CryptoJS.enc.Utf8.parse(key), { //key必须要为16位十六进制进制
                iv: CryptoJS.enc.Utf8.parse(vi), // 初始向量
                mode: CryptoJS.mode.CBC,
                padding: CryptoJS.pad.Pkcs7
            });
            return CryptoJS.enc.Base64.stringify(encrypted.ciphertext); //将密文转换为Base64编码字符串
        },
        //AES解密
        decryptWithAES: function(encrypted_Base64, key, vi) {

            let encrypted = CryptoJS.enc.Base64.parse(encrypted_Base64); //Base64字符串解码
            let src = CryptoJS.enc.Base64.stringify(encrypted);

            let decrypted = CryptoJS.AES.decrypt(src, CryptoJS.enc.Utf8.parse(key), {
                iv: CryptoJS.enc.Utf8.parse(vi), // 初始向量
                mode: CryptoJS.mode.CBC,
                padding: CryptoJS.pad.Pkcs7
            });
            return decrypted.toString(CryptoJS.enc.Utf8);
        }

    }
});