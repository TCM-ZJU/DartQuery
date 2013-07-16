package business.delegates
{
	import com.adobe.cairngorm.business.ServiceLocator;
	
	import mx.rpc.AsyncToken;
	import mx.rpc.IResponder;
	import mx.rpc.http.HTTPService;
	import mx.controls.Alert;
	
	public class RecordSearchDelegate
	{
		private var serviceLocator:ServiceLocator = ServiceLocator.getInstance();
		private var _service:HTTPService;
		private var _responder:IResponder;
		
		public function RecordSearchDelegate(responder:IResponder)
		{
			_service = serviceLocator.getHTTPService("recordSearchService");
			_responder = responder;
		}
		
		public function search(key:String, source:String, target:String, pageIndex:int, pageSize:int, searchType:int):void
		{
		//	Alert.show("relation");
			var param:Object = new Object();
			param.key = key;
			param.source = source;
			param.target = target;
			param.index = pageIndex;
			param.size = pageSize;
			param.type = searchType;
			var token:AsyncToken = _service.send(param);
			token.addResponder(_responder);
		}

	}
}