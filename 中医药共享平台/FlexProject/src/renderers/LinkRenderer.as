package renderers
{
	import events.LinkButtonDynamicEvent;
	
	import flash.events.MouseEvent;
	
	import mx.controls.LinkButton;

	public class LinkRenderer extends LinkButton
	{
		[Bindable]
        private var _rowObject:Object = new Object();
		
		[Bindable]
		private var _columnLabel:String = "";
		
		[Bindable]
		private var _source:String = "";
		
		
		public function LinkRenderer()
		{
			super();
			this.label = "查看";
			this.setStyle("textDecoration","underline");
            this.setStyle("textAlign","center");
            this.setStyle("fontWeight", "normal");
            this.setStyle("color", "green");
            this.addEventListener(MouseEvent.CLICK,linkButtonClickHandler);
		}
		
		override public function set data(value:Object):void
        {
            super.data = value;
       //     this.label = data.primary;
            // Make sure there is data
            if (value != null) {       
                this._rowObject = value;         
            }
        }
        
        [Bindable]
        public function set columnLabel(value:String):void
        {
        	this._columnLabel = value;
        }
        public function get columnLabel():String
        {
        	return this._columnLabel;
        }
        
       	[Bindable]
        public function set source(value:String):void
        {
        	this._source = value;
        }
        public function get source():String
        {
        	return this._source;
        }
              
		
		public function linkButtonClickHandler(e:MouseEvent):void
		{
			//Alert.show("in renderer");
			var linkButtonEvent:LinkButtonDynamicEvent = 
				new LinkButtonDynamicEvent(LinkButtonDynamicEvent.EVENT_ID, _rowObject.primary, _source, _columnLabel);
			dispatchEvent(linkButtonEvent);
		}
		
	}
}