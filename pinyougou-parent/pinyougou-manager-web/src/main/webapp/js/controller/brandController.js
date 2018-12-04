app.controller("brandController",function($scope,$http,$controller,brandService) {
	$controller("baseController",{$scope:$scope});
		//<!--分页查询所有--> 
		$scope.findPage = function(page, rows) {
			brandService.findPage(page, rows).success(
				function(response) {
					$scope.list = response.rows;
					 $scope.paginationConf.totalItems = response.total; 
				});
		}
		
		
		$scope.searchEntity={};
		$scope.search = function(page, rows) {
			brandService.search(page, rows,$scope.searchEntity).success(
				function(response) {
					$scope.list = response.rows;
					 $scope.paginationConf.totalItems = response.total; 
				});
		}
		
		
	
		
		//<!--保存方法--> 
		$scope.save=function(){
			var object =brandService.add($scope.entity); 
			if($scope.entity.id!=null){
				object=brandService.update($scope.entity);
			}
			object.success(
				function(response){
					if(response.success){
						$scope.reloadList();//重新加载
					}else{
						alert("添加失败");
					}
				}		
			)
		}
		
		//<!--修改先查询 查询一个-->
		$scope.findOne=function(id){
			brandService.findOne(id).success(
				function(response){
						$scope.entity=response;
				})
				
		}
		
	
		
		//删除
		$scope.dele=function(){
			brandService.dele($scope.selectIds).success(
					function(response){
						if(response.success){
							$scope.reloadList();//重新加载
						}else{
							alert("删除失败");
						}
					})
		}
	})