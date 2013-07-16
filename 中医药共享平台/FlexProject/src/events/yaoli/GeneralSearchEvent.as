package events.yaoli
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import flash.events.Event;
	
	public class GeneralSearchEvent extends CairngormEvent
	{
		public static const EVENT_ID:String = "GeneralSearch";
		public var key:String;
		public var source:String;
		public var related:String;
		public var cate:String;
		
		public function GeneralSearchEvent(key:String, cate:String, source:String, related:String)
		{
			this.key = key; 
			this.source = source;
			this.related = related;
			this.cate = cate;
			super(EVENT_ID);
		}
		
		override public function clone():Event
		{
			return new GeneralSearchEvent(key, cate, source, related);
		}
	}
}