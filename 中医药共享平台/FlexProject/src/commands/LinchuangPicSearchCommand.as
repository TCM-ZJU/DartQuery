package commands
{
	import business.delegates.LinchuangPicSearchDelegate;
	
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import events.LinchuangPicSearchEvent;
	
	import flexlib.containers.SuperTabNavigator;
	
	import model.TsfModelLocator;
	
	import mx.controls.Alert;
	import mx.rpc.IResponder;
	
	import view.LinchuangPicView;
	
	public class LinchuangPicSearchCommand implements ICommand, IResponder
	{
		[Bindable]
		private var modelLocator:TsfModelLocator = TsfModelLocator.getInstance();
		
		public var key:String;
		public var source:String;
		public var related:String;
		public var pageIndex:int;
		public var pageSize:int;
		public var searchType:int;
		
		public function execute(event:CairngormEvent):void
		{
			var linchuangPicSearchEvent:LinchuangPicSearchEvent = event as LinchuangPicSearchEvent;
			key = linchuangPicSearchEvent.key;
			source = linchuangPicSearchEvent.source;
			related = linchuangPicSearchEvent.related;
			pageIndex = linchuangPicSearchEvent.pageIndex;
			pageSize = linchuangPicSearchEvent.pageSize;
			searchType = modelLocator.searchType;
			var delegate:LinchuangPicSearchDelegate = new LinchuangPicSearchDelegate(this);
			delegate.search(key, source, related, pageIndex, pageSize, searchType);
		}
		
		public function result(data:Object):void
		{
			//Alert.show("in result");
			if (data.result != null)
			{
				if (data.result is XML)
				{
					//Alert.show(data.result.toString());
					var resXML:XML = data.result as XML;
					var count:int = Number(resXML..count);
					if (count <= 0) {
						Alert.show("无原文图片");
						return;
					}
					var tabNav:SuperTabNavigator = modelLocator.resultView.tabNav;	
					var picView:LinchuangPicView = new LinchuangPicView();
					picView.label = "原文图片";
			//		picView.init(resXML);
					tabNav.addChild(picView);
					tabNav.selectedChild = picView;
					tabNav.selectedChild.name = resXML..key + "原文图片"; 
					picView.init(resXML);
				}
			}
		}
		
		public function fault(info:Object):void
		{
			Alert.show(info.fault.message, "错误");
		}
		
		public function LinchuangPicSearchCommand()
		{
		}

	}
}