package business.delegates.yaoli
{
	import com.adobe.cairngorm.business.ServiceLocator;
	import mx.rpc.AsyncToken;
	import mx.rpc.IResponder;
	import mx.rpc.http.HTTPService;
	import mx.controls.Alert;
	
	public class GeneralSearchDelegate
	{
		private var serviceLocator:ServiceLocator = ServiceLocator.getInstance();
		private var _service:HTTPService;
		private var _responder:IResponder;
		
		public function GeneralSearchDelegate(responder:IResponder)
		{
			_service = serviceLocator.getHTTPService("generalSearchService");
			_responder = responder;
		}
		
		public function search(key:String, cate:String, source:String, target:String, searchType:int):void
		{
		//	Alert.show("relation");
			var param:Object = new Object();
			param.key = key;
			param.cate = cate;
			param.source = source;
			param.target = target;
			param.type = searchType;
			var token:AsyncToken = _service.send(param);
			token.addResponder(_responder);
		}

	}
}