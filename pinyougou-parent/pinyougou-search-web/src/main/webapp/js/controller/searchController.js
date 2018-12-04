app.controller('searchController',function($scope,$location,searchService){
	
	$scope.searchMap={"keywords":"","brand":"","category":"","spec":{},"price":"","pageNo":1,"pageSize":20,"sortType":"","sortField":""}
	
	$scope.search=function(){
		$scope.searchMap.pageNo=parseInt($scope.searchMap.pageNo);
		searchService.search($scope.searchMap).success(
				function(response){
			$scope.searchResult=response;
			bulidPageLable();
		});
	}
	//$scope.searchMap={"brand":"","category":"","spec":{"网络":"移动4G","机身内存":"32G"}}
	//面包屑添加选项
	$scope.addSearchItem=function(key,value){
		if(key=="brand"||key=="category"||key=="price"){
			$scope.searchMap[key]=value;
		}else{
			$scope.searchMap.spec[key]=value;
		};
		$scope.search();
	}
	//面包屑删除选项
	$scope.deleteSearchItem=function(key){
		if(key=="brand"||key=="category"||key=="price"){
			$scope.searchMap[key]="";
		}else{
			delete $scope.searchMap.spec[key];
		};
		$scope.search();
		
	}
	$scope.isFirstPage=true;//前面有点
	$scope.isLastPage=true;//后面有点
	//建立索引表
	bulidPageLable=function(){
		$scope.pageLable=[];
		var maxPageNo=$scope.searchResult.totalPages;
		var pageNo = $scope.searchMap.pageNo;
		var firstPage=1;
		var lastPage=maxPageNo;
		//1234567891011
		if(maxPageNo>5){
			if(pageNo<=3){
				lastPage=5;
				$scope.isFirstPage=false;//前面有点
//				$scope.isLastPage=true;
			}else if(pageNo>maxPageNo-2){
				firstPage=maxPageNo-4;
				$scope.isFirstPage=true;
				$scope.isLastPage=false;
			}else{
				firstPage=pageNo-2;
				lastPage=pageNo+2;
				$scope.isFirstPage=true;
				$scope.isLastPage=true;
			}
		}else{
			$scope.isLastPage=false;
			$scope.isFirstPage=false;
		}
		//建立索引表
		for(var i =firstPage;i<=lastPage;i++){
			$scope.pageLable.push(i);
		}
	}
	$scope.queryByPage=function(page){
		if(page<0||page>$scope.searchResult.totalPages){
			return;
		}
		$scope.searchMap.pageNo=page;
		$scope.search();
	}
	
	$scope.isTopPage=function(){
		if($scope.searchMap.pageNo==1){
			return true;
			
		}else{
			return false;
		}
	}
	$scope.isEndPage=function(){
		if($scope.searchMap.pageNo==$scope.searchResult.totalPages){
			return true;
		}else{
			return false;
		}
	}
	
	$scope.order=function(sortType,sortField){
		$scope.searchMap.sortType =	sortType;
		$scope.searchMap.sortField = sortField;
	$scope.search();
	}
	
	$scope.keywordsIsBrand=function(){
		
		for(var i=0;i<$scope.searchResult.brandList.length;i++){
			if($scope.searchMap.keywords.indexOf($scope.searchResult.brandList[i].text)>0){
				return true
				
			}else{
				return false;
			}
		}
	}
	$scope.loadkeywords=function(){
		$scope.searchMap.keywords= $location.search()['keywords'];
		$scope.search();
		}
	
	
});