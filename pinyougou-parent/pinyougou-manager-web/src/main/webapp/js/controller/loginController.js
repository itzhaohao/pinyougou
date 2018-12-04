app.controller("loginController",function($scope,$http){
	
	$scope.showName=function(){
		$http.get("../login/showName.do").success(
				function(response){
					$scope.loginName=response.loginName;
			
		});
	}
})