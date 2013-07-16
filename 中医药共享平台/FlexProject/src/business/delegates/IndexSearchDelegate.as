package business.delegates
{
	import com.adobe.cairngorm.business.ServiceLocator;
	import mx.rpc.AsyncToken;
	import mx.rpc.IResponder;
	import mx.rpc.http.HTTPService;
	public class IndexSearchDelegate
	{
		private var serviceLocator:ServiceLocator = ServiceLocator.getInstance();
		private var _service:HTTPService;
		private var _responder:IResponder;
		
		public function IndexSearchDelegate(responder:IResponder)
		{
			_service = serviceLocator.getHTTPService("indexSearchService");
			_responder = responder; 
		}
		
		public function search(key:String, source:String, pageIndex:int, pageSize:int):void
		{
			var param:Object = new Object();
			param.key = key;
			param.source = source;
			param.index = pageIndex;
			param.size = pageSize;
			var token:AsyncToken = _service.send(param);
			token.addResponder(_responder);
		}

	}
}