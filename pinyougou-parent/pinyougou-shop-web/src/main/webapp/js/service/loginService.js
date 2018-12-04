app.service('loginService',function($http){
	this.showName=function(){
		return $http.post('../login/showName.do');
	}
	
	
	
})
