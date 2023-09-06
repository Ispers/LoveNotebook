export default {
	
	set(name,value){
		uni.setStorageSync(name,value);
	},
	
	setJson(name,value){
		uni.setStorageSync(name,JSON.stringify(value));
	},
	
	setToken(token){
		uni.setStorageSync('token',token)
	},
	
	get(name){
		return uni.getStorageSync(name);
	},
	
	getJson(name){
		const content = uni.getStorageSync(name);
		if(!content){
			return null;
		}
		return JSON.parse(content);
	},
	
	getToken(){
		const content = uni.getStorageSync('token');
		if(!content){
			return null;
		}
		return content;
	},
	
	remove(name){
		uni.removeStorageSync(name);
	},
	
	clear(){
		uni.clearStorageSync();
	}
};
