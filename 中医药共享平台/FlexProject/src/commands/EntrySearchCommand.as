package commands
{
	
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import events.EntrySearchEvent;
	
	import model.TsfModelLocator;
	
	import mx.collections.ArrayCollection;
	
	import vo.GeneralItem;

	public class EntrySearchCommand implements ICommand
	{
		[Bindable]
		private var modelLocator:TsfModelLocator = TsfModelLocator.getInstance();
		
		public function execute(event:CairngormEvent):void
		{
			trace("execute");
			var entrySearchEvent:EntrySearchEvent = event as EntrySearchEvent;
			
			var item:GeneralItem = new GeneralItem();
			item.count = 100;
			item.dbName = "基本信息";
			if (modelLocator.currentInfo.subEntry != "")
				item.text = "该" + modelLocator.currentInfo.subEntry + "的" + item.dbName + "有" + item.count + "条";
			else
				item.text = "该" + modelLocator.currentInfo.primaryEntry + "的" + item.dbName + "有" + item.count + "条";
			
			var item2:GeneralItem = new GeneralItem();
			item2.count = 120;
			item2.dbName = "对照组";
			if (modelLocator.currentInfo.subEntry != "")
				item2.text = "该" + modelLocator.currentInfo.subEntry + "的" + item2.dbName + "有" + item2.count + "条";
			else
				item2.text = "该" + modelLocator.currentInfo.primaryEntry + "的" + item2.dbName + "有" + item2.count + "条";
		/*		
			var item3:GeneralItem = new GeneralItem();
			item3.count = 130;
			item3.dbName = "模型组";
			if (modelLocator.currentInfo.entry2 != "")
				item3.text = "该" + modelLocator.currentInfo.entry2 + "的" + item3.dbName + "有" + item3.count + "条";
			else
				item3.text = "该" + modelLocator.currentInfo.entry1 + "的" + item3.dbName + "有" + item3.count + "条";	
			*/	
			var items:Array = new Array();
			items.push(item);
			items.push(item2);
		//	items.push(item3);
			modelLocator.generalResult.items = new ArrayCollection(items);
			
		}

	}
}