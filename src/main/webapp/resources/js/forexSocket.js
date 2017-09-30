$(document).ready(function() {
	var gSystem = {};
	var initPage = function () {
		$('.chart').easyPieChart({
	        barColor: '#f8ac59',
//	                scaleColor: false,
	        scaleLength: 5,
	        lineWidth: 4,
	        size: 80
	    });

	    $('.chart2').easyPieChart({
	        barColor: '#1c84c6',
//	                scaleColor: false,
	        scaleLength: 5,
	        lineWidth: 4,
	        size: 80
	    });

	    var data2 = [
	        [gd(2012, 1, 1), 7], [gd(2012, 1, 2), 6], [gd(2012, 1, 3), 4], [gd(2012, 1, 4), 8],
	        [gd(2012, 1, 5), 9], [gd(2012, 1, 6), 7], [gd(2012, 1, 7), 5], [gd(2012, 1, 8), 4],
	        [gd(2012, 1, 9), 7], [gd(2012, 1, 10), 8], [gd(2012, 1, 11), 9], [gd(2012, 1, 12), 6],
	        [gd(2012, 1, 13), 4], [gd(2012, 1, 14), 5], [gd(2012, 1, 15), 11], [gd(2012, 1, 16), 8],
	        [gd(2012, 1, 17), 8], [gd(2012, 1, 18), 11], [gd(2012, 1, 19), 11], [gd(2012, 1, 20), 6],
	        [gd(2012, 1, 21), 6], [gd(2012, 1, 22), 8], [gd(2012, 1, 23), 11], [gd(2012, 1, 24), 13],
	        [gd(2012, 1, 25), 7], [gd(2012, 1, 26), 9], [gd(2012, 1, 27), 9], [gd(2012, 1, 28), 8],
	        [gd(2012, 1, 29), 5], [gd(2012, 1, 30), 8], [gd(2012, 1, 31), 25]
	    ];

	    var data3 = [
	        [gd(2012, 1, 1), 800], [gd(2012, 1, 2), 500], [gd(2012, 1, 3), 600], [gd(2012, 1, 4), 700],
	        [gd(2012, 1, 5), 500], [gd(2012, 1, 6), 456], [gd(2012, 1, 7), 800], [gd(2012, 1, 8), 589],
	        [gd(2012, 1, 9), 467], [gd(2012, 1, 10), 876], [gd(2012, 1, 11), 689], [gd(2012, 1, 12), 700],
	        [gd(2012, 1, 13), 500], [gd(2012, 1, 14), 600], [gd(2012, 1, 15), 700], [gd(2012, 1, 16), 786],
	        [gd(2012, 1, 17), 345], [gd(2012, 1, 18), 888], [gd(2012, 1, 19), 888], [gd(2012, 1, 20), 888],
	        [gd(2012, 1, 21), 987], [gd(2012, 1, 22), 444], [gd(2012, 1, 23), 999], [gd(2012, 1, 24), 567],
	        [gd(2012, 1, 25), 786], [gd(2012, 1, 26), 666], [gd(2012, 1, 27), 888], [gd(2012, 1, 28), 900],
	        [gd(2012, 1, 29), 178], [gd(2012, 1, 30), 555], [gd(2012, 1, 31), 993]
	    ];

	    function gd(year, month, day) {
	        return new Date(year, month - 1, day).getTime();
	    }

	    var previousPoint = null, previousLabel = null;

	    var mapData = {
	        "US": 298,
	        "SA": 200,
	        "DE": 220,
	        "FR": 540,
	        "CN": 120,
	        "AU": 760,
	        "BR": 550,
	        "IN": 200,
	        "GB": 120,
	    };

	    $('#world-map').vectorMap({
	        map: 'world_mill_en',
	        backgroundColor: "transparent",
	        regionStyle: {
	            initial: {
	                fill: '#e4e4e4',
	                "fill-opacity": 0.9,
	                stroke: 'none',
	                "stroke-width": 0,
	                "stroke-opacity": 0
	            }
	        },

	        series: {
	            regions: [{
	                values: mapData,
	                scale: ["#1ab394", "#22d6b1"],
	                normalizeFunction: 'polynomial'
	            }]
	        },
	    });
	    
	    //myself
	    console.log('init page');
	};
	var myInitPage = function () {
		console.log('my init page');
		var mainChart = $('#main-chart-forex');
		createChartObject('flot-dashboard-chart', mainChart.width(), mainChart.height(), 0, null);
//		$.ajax({
//			url : "forex/dataWithWave/EURUSD",
//			data : 'data',
//			type : 'GET',
//			contentType : "application/json",
//			xhrFields: {
//				  withCredentials: true
//			}
//		}).done(function(val) {
//			console.log(val);
////			gSystem.chart.dataProvider = val;
////			gSystem.chart.validateData();
//			mainChart.empty();
//			mainChart.append('<div class="flot-chart-content" id="flot-dashboard-chart"></div>');
//			
//			createChartObject('flot-dashboard-chart', mainChart.width(), mainChart.height(), val.dataList, val.indexList);
//			
//		}).fail(function(val) {
//			console.log(val);
//		});
		var socket = new SockJS("/ws");
		var stompClient = Stomp.over(socket);
		
		var updatePrice = function(frame) {
			var complexData = JSON.parse(frame.body);
			$('#flot-dashboard-chart').empty();
			createChartObject('flot-dashboard-chart', mainChart.width(), mainChart.height(), complexData.dataList, complexData.indexList);
			//update info predict larger
			$('#current-mean').html(complexData.stat.mean);
			$('#current-sd').html(complexData.stat.sd);
			$('#predict-larger').html(complexData.content);
		};
		
		var connectCallback = function() {
			stompClient.send("/app/listening");
			stompClient.subscribe('/topic/forex/EURUSD/5', updatePrice);
		};
		var errorCallback = function(error) {
			alert(error.headers.message);
		};
		stompClient.connect("guest", "guest", connectCallback, errorCallback);
	};
	
	initPage();
	myInitPage();
});
var createChartObject = function (domObjStr, requiredWidth, requiredHeight, oriData, oriWave) {
	var margin = {top: 20, right: 20, bottom: 30, left: 50},
    width = requiredWidth - margin.left - margin.right,
    height = requiredHeight - margin.top - margin.bottom;

	var parseDate = d3.time.format("%d-%b-%y").parse;
	
	var x = techan.scale.financetime()
	    .range([0, width]);
	
	var y = d3.scale.linear()
	    .range([height, 0]);
	
	var candlestick = techan.plot.candlestick()
	    .xScale(x)
	    .yScale(y);
	
	var tradearrow = techan.plot.tradearrow()
	    .xScale(x)
	    .yScale(y)
	    .orient(function(d) { return d.type.startsWith("buy") ? "up" : "down"; });
	
	var xAxis = d3.svg.axis()
	    .scale(x)
	    .orient("bottom");
	
	var yAxis = d3.svg.axis()
	    .scale(y)
	    .orient("left");
	var ohlcAnnotation = techan.plot.axisannotation()
	    .axis(yAxis)
	    .format(d3.format(',.5fs'));
	var timeAnnotation = techan.plot.axisannotation()
	    .axis(xAxis)
	    .format(d3.time.format('%Y-%m-%d'))
	    .width(65)
	    .translate([0, height]);
	
	var svg = d3.select("#" + domObjStr).append("svg")
	    .attr("width", width + margin.left + margin.right)
	    .attr("height", height + margin.top + margin.bottom)
	.append("g")
	    .attr("transform", "translate(" + margin.left + "," + margin.top + ")");
	
	var crosshair = techan.plot.crosshair()
	    .xScale(x)
	    .yScale(y)
	    .xAnnotation(timeAnnotation)
	    .yAnnotation(ohlcAnnotation);
	
	svg.append("clipPath")
	    .attr("id", "clip")
	.append("rect")
	    .attr("x", 0)
	    .attr("y", y(1))
	    .attr("width", width)
	    .attr("height", y(0) - y(1));
	
	svg.append("g")
	    .attr("class", "candlestick")
	    .attr("clip-path", "url(#clip)");
	
	svg.append("g")
	    .attr("class", "x axis")
	    .attr("transform", "translate(0," + height + ")");
	
	svg.append("g")
	    .attr("class", "y axis")
	.append("text")
	    .attr("transform", "rotate(-90)")
	    .attr("y", 6)
	    .attr("dy", ".71em")
	    .style("text-anchor", "end")
	    .text("");
	
	svg.append('g')
    	.attr("class", "crosshair");
	
	var draw = function () {
		svg.select("g.candlestick").call(candlestick);
		// using refresh method is more efficient as it does not perform any data joins
		// Use this if underlying data is not changing
		//svg.select("g.candlestick").call(candlestick.refresh);
		svg.select("g.x.axis").call(xAxis);
		svg.select("g.y.axis").call(yAxis);
		svg.select("g.tradearrow").call(tradearrow.refresh);
	}
		
	var zoom = d3.behavior.zoom()
	    .on("zoom", draw);		
	svg.select('g.crosshair').call(crosshair).call(zoom);
	
	var data = oriData;
	if (data === 0) {
		data = [
				{ date: '2015-04-06 09:30', open: 62.40, high: 63, low: 62, close: 63 },
				{ date: '2015-04-06 09:35', open: 62.40, high: 63, low: 62, close: 63 },
				{ date: '2015-04-06 09:40', open: 62.40, high: 63, low: 62, close: 63 },
				{ date: '2015-04-06 09:45', open: 63, high: 63.5, low: 62.5, close: 63.5 },
				{ date: '2015-04-06 09:50', open: 63.5, high: 63.8, low: 62.8, close: 62.8 },
				{ date: '2015-04-06 09:55', open: 62.8, high: 62.8, low: 61.8, close: 61.8 },
				{ date: '2015-04-06 10:00', open: 63.5, high: 63.8, low: 62.8, close: 62.8 },
				{ date: '2015-04-06 10:05', open: 63.5, high: 63.8, low: 62.8, close: 62.8 },
			];
	}
	
	var dateConverter = function (str) {
		var year = parseInt(str.substring(0,4));
		var month = parseInt(str.substring(5,7));
		var day = parseInt(str.substring(8,10));
		var hour = parseInt(str.substring(11,13));
		var minute = parseInt(str.substring(14,16));
		return new Date(year, month - 1, day, hour, minute, 0, 0);
	};
	
	var accessor = candlestick.accessor();
	data = data/*.slice(0,200)*/.map(function(d) {
	return {
	    date: dateConverter(d.date),
	    open: +d.open,
	    high: +d.high,
	    low: +d.low,
	    close: +d.close
	    //volume: +d.volume
	};
	}).sort(function(a, b) { return d3.ascending(accessor.d(a), accessor.d(b)); });
	
	x.domain(data.map(accessor.d));
	y.domain(techan.scale.plot.ohlc(data, accessor).domain());
	
	if (oriWave !== null) {
		var trades = oriWave.map(function(unit) {
			var isMin = true;
			if (unit.type === 'sell') {
				isMin = false;
			}
			return {
				date: data[unit.index].date, type: unit.type, 
				price: (isMin ? data[unit.index].low : data[unit.index].high)
			};
		});
		svg.append("g")
	      .datum(trades)
	      .attr("class", "tradearrow")
	      .call(tradearrow);
	}
	
	svg.select("g.candlestick").datum(data);
	var zoomable = x.zoomable();
	if (data.length > 200) {
		var mid = data.length / 2;
		var standard = 960 / (500 * 200);
		var real = width / height;
		var numberSampleShow = real / standard;
		zoomable.domain([data.length - numberSampleShow, data.length]);
	}
	
	draw();
	
	// Associate the zoom with the scale after a domain has been applied
	zoom.x(zoomable.clamp(false)).y(y);
};