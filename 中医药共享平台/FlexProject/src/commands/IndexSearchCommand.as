package commands
{
	import com.adobe.cairngorm.commands.ICommand;
	
	import mx.rpc.IResponder;
	import events.IndexSearchEvent;
	import business.delegates.IndexSearchDelegate;
	import model.TsfModelLocator;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import view.DisplayInfoView;
	import view.LCYJDisplayInfoView;
	import view.TreatInfoDisplayView;
	import flexlib.containers.SuperTabNavigator;
	import mx.controls.Alert;
	
	public class IndexSearchCommand implements ICommand, IResponder
	{
		[Bindable]
		private var modelLocator:TsfModelLocator = TsfModelLocator.getInstance();
		
		public var key:String;
		public var source:String;
		public var pageIndex:int;
		public var pageSize:int;
		
		public function execute(event:CairngormEvent):void
		{
			var indexSearchEvent:IndexSearchEvent = event as IndexSearchEvent;
			key = indexSearchEvent.key;
			source = indexSearchEvent.source;
			pageIndex = indexSearchEvent.pageIndex;
			pageSize = indexSearchEvent.pageSize;
			var delegate:IndexSearchDelegate = new IndexSearchDelegate(this);
			delegate.search(key, source, pageIndex, pageSize);
		}
		
		public function result(data:Object):void
		{
			//Alert.show(data.toString());
			if (data.result != null)
			{
				if (data.result is XML)
				{				
					var tabNavigate:SuperTabNavigator = modelLocator.resultView.tabNav;
					var displayView:Object = tabNavigate.getChildAt(tabNavigate.selectedIndex);
					if (displayView is LCYJDisplayInfoView) {
						var lcyjDisplayInfoView:LCYJDisplayInfoView = displayView as LCYJDisplayInfoView;
						lcyjDisplayInfoView.showRecord(data.result as XML);
					}
					else if (displayView is TreatInfoDisplayView) {
						var treatInfoDisplayView:TreatInfoDisplayView = displayView as TreatInfoDisplayView;
						treatInfoDisplayView.showRecord(data.result as XML);
					}
					else if (displayView is DisplayInfoView) {
						var displayInfoView:DisplayInfoView = displayView as DisplayInfoView;
						displayInfoView.init(data.result as XML);
					}
				//	var displayView:LCYJDisplayInfoView = tabNavigate.getChildAt(tabNavigate.selectedIndex) as LCYJDisplayInfoView;
				//	displayView.showRecord(data.result as XML);			
				}
			}
		
		}
		
		public function fault(info:Object):void
		{
			Alert.show(info.fault.message, "错误");
		}
		
		public function IndexSearchCommand() 
		{
		}
	}
}