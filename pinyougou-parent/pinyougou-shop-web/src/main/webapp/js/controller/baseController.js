app.controller("baseController",function($scope){
	//<!--前端分页插件 重新加载-->
	$scope.reloadList = function() {
		$scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
	}
	
	//<!--前端分页插件--> 
	$scope.paginationConf = {
			currentPage : 1,
			totalItems : 10,
			itemsPerPage : 10,
			perPageOptions : [ 10, 20, 30, 40, 50 ],
			onChange : function() {
				$scope.reloadList();//重新加载
			}
	}
	
	//<!--复选框 id集合-->
	$scope.selectIds=[];
	$scope.updateSelection=function($event,id){
		if($event.target.checked){
		$scope.selectIds.push(id);
		}else{
			var index = $scope.selectIds.indexOf(id);
			$scope.selectIds.splice(index,1);
		}
	}
	//提取 json 字符串数据中某个属性，返回拼接字符串 逗号分隔
	$scope.jsonToString=function(jsonString,key){
		var json = JSON.parse(jsonString);//将 json 字符串转换为 json 对象
		var value = "";
		for(i=0;i<json.length;i++){
			if(i>0){
				value+=",";
			}
			value+=json[i][key];
		}
		return value;
		
	}
	//判断集合中key是否存在相应的value值
	$scope.selectObjecByKey=function(list,key,keyValue){
		for(var i=0;i<list.length;i++){
			if(list[i][key]==keyValue){
			return list[i];	
			}
		}
		return null;
		
	}
	
})