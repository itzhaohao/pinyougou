var app = angular.module("pinyougou", []);
//过滤器 使用了$sce服务
app.filter('trustHtml',['$sce',function($sce){
	return function(data){//传入参数时被过滤的内容
		return $sce.trustAsHtml(data);//返回的是过滤的内容,信任html的转换
	}
}]);