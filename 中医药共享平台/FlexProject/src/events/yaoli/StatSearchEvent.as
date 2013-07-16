package events.yaoli
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import flash.events.Event;
	
	public class StatSearchEvent extends CairngormEvent
	{
		public static const EVENT_ID:String = "StatSearch";
		public var key:String;
		public var source:String;
		public var related:String;
		public var cate:String;
		public var field:String;
		public var pageIndex:int;
		public var pageSize:int;
		public var identity:int;
		
		public function StatSearchEvent(key:String, cate:String, source:String, related:String, field:String,
		 	pageIndex:int, pageSize:int, identity:int)
		{
			this.key = key;
			this.source = source;
			this.related = related;
			this.cate = cate;
			this.field = field;
			this.pageIndex = pageIndex;
			this.pageSize = pageSize;
			this.identity = identity;
			super(EVENT_ID);
		}
		
		override public function clone():Event
		{
			return new StatSearchEvent(key, cate, source, related, field, pageIndex, pageSize, identity);
		}

	}
}