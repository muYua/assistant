/**
 * JS工具包
 * @author MUYUA
 */

//判断是否按下回车
function isPressEnter() {
	if (event.keyCode == 13) { //判断是否按下回车
		return true;
	}
	else{
		return false;
	}
}

//判断字符串是否为空
function isEmpty (obj) {
	//typeof obj == "undefined"，判断obj是否定义
    if(typeof obj == "undefined" || obj == null || obj == ""){
        return true;
    }
    else{
        return false;
    }
}

//判断手机号合法性
function isPhoneNumber (str) {
	var phone = /^1[3-9]\d{9}$/;
	return phone.test(str);
}

//判断电子邮件合法性
function isEmail (str) {
	var email = /^[a-zA-Z\d](\w|\-|\+)+@(qq|163|126|yeah|gmail|outlook|hotmail|live|aliyun)\.(com|net)$/;
	return email.test(str);
}

//判断密码合法性
function isPassword (str) {
	var pswd = /^[\S]{6,16}$/;
	return pswd.test(str);
}

/* 数据加密 */
//需要引入crypto-js.js

//Hashing加密
function encryptWithHashing (message, type) {
	switch (type) {
		case "SHA256":
			return CryptoJS.SHA256(message+type).toString(CryptoJS.enc.Base64);
//			break;
		case "MD5":
			return CryptoJS.MD5(message+type).toString(CryptoJS.enc.Base64);
		default:
			return null;
	}
}


/* AES加解密 */
//AES加密
function encryptWithAES (message, key, vi) {
	var encrypted = CryptoJS.AES.encrypt(CryptoJS.enc.Utf8.parse(message), CryptoJS.enc.Utf8.parse(key), { //key必须要为16位十六进制进制
		iv: CryptoJS.enc.Utf8.parse(vi), // 初始向量
		mode: CryptoJS.mode.CBC,
		padding: CryptoJS.pad.Pkcs7 
	});
	return CryptoJS.enc.Base64.stringify(encrypted.ciphertext); //将密文转换为Base64编码字符串
}

//AES解密
function decryptWithAES (encrypted_Base64, key, vi) {
	
	let encrypted = CryptoJS.enc.Base64.parse(encrypted_Base64); //Base64字符串解码
	let src = CryptoJS.enc.Base64.stringify(encrypted);
	
    var decrypted = CryptoJS.AES.decrypt(src, CryptoJS.enc.Utf8.parse(key), { 
    	iv: CryptoJS.enc.Utf8.parse(vi), // 初始向量
    	mode: CryptoJS.mode.CBC,
    	padding: CryptoJS.pad.Pkcs7
    });
    return decrypted.toString(CryptoJS.enc.Utf8);
}
