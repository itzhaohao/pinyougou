app.service('userService',function($http){
	
	this.add=function(user,checkCode){
	return $http.post('user/add.do?checkCode='+checkCode,user);	
		
	}
	this.sendCode=function(phone){
		return $http.get('user/sendCode.do?phone='+phone);
	}
	
	
	
});