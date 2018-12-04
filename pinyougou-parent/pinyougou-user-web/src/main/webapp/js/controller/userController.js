app.controller('userController',function($scope,userService){
	//注册信息添加至数据库
	$scope.add=function(){
		if($scope.password!=$scope.entity.password){
			return;
		}
		userService.add($scope.entity,$scope.checkCode).success(
				function(response){
			if(response.success){
				alert(response.message);
			}
		});
	}
	
	$scope.sendCode=function(){
		if($scope.entity.phone==null){
			alert("请输入手机号");
			return;
		}
		userService.sendCode($scope.entity.phone).success(
				function(response){
					alert(response.message);
				});
	}
	
});