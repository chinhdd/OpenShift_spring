var gSystem = {};
$(document).ready(function() {
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
		$('#data_5 .input-daterange').datepicker({
			keyboardNavigation: false,
			forceParse: false,
			autoclose: true
		});
		$('.clockpicker').clockpicker();
		$('#setting-loading').hide();
		
		$('#forex-selection').change(function() {
			var val = $(this).val();
			$('#chart-title').html(val);
			if (val === 'EURUSD') {
				$('#label-duration').html('Data available from 04/18/2016 09:00 to 04/22/2016 23:55');
				$('#input-start-date').val('04/18/2016');
				$('#input-end-date').val('04/22/2016');
				$('#input-start-time').val('09:00');
				$('#input-end-time').val('23:55');
			} else if (val === 'TAIEX') {
				$('#label-duration').html('Data available from 02/28/2004 09:00 to 12/31/2004 23:55');
				$('#input-start-date').val('02/28/2004');
				$('#input-end-date').val('12/31/2004');
				$('#input-start-time').val('09:00');
				$('#input-end-time').val('23:55');
			}
		});
		
		$('#start-testing-btn').prop('disabled',true);
		$('#stop-testing-btn').prop('disabled',true);
		$('#setting-save-btn').click(function () {
			console.log('click');
			var data = $('#setting-form').serializeArray();
			console.log(data);
			var entity = {};
			for (var i = 0; i < data.length; i++) {
				entity[data[i].name] = data[i].value;
			}
			console.log(entity);
			getForexData(entity);
			$(this).prop('disabled',true);
		});
		
		var getForexData = function (obj) {
			$('#setting-loading').show();
			gSystem.name = obj.name;
			$.ajax({
				url : "testing/data",
				data : obj,
				type : 'GET',
				contentType : "application/json",
				xhrFields: {
					  withCredentials: true
				}
			}).done(function(val) {
				console.log(val);
				$('#setting-save-btn').prop('disabled',false);
				$('#setting-loading').hide();
				var mainChart = $('#main-chart-forex');
				mainChart.empty();
				mainChart.append('<div class="flot-chart-content" id="main-chart-forex-content"></div>');
				createChartObject('main-chart-forex-content', mainChart.width(), mainChart.height(), val.dataList, val.indexList, false);
				var sp = val.content.split(";");
				gSystem.lastDate = sp[0];
				gSystem.finalDate = sp[1];
				gSystem.trend = [];
				gSystem.actualTrend = [];
				$('#start-testing-btn').prop('disabled',false);
			}).fail(function(val) {
				console.log(val);
				$('#setting-save-btn').prop('disabled',false);
				$('#setting-loading').hide();
			});
		};
		$('#start-testing-btn').click(function() {
			gSystem.running = true;
			//gSystem.dates = [];
			prepareGetMoreData();
			$(this).prop('disabled',true);
			$('#stop-testing-btn').prop('disabled',false);
		});
		$('#stop-testing-btn').click(function() {
			gSystem.running = false;
			$(this).prop('disabled',true);
			$('#start-testing-btn').prop('disabled',false);
		});
		var prepareGetMoreData = function() {
			var obj = {};
			obj.name = gSystem.name;
			obj.lastDate = gSystem.lastDate;
			obj.finalDate = gSystem.finalDate;
			getMoreForexData(obj);
		};
		var getMoreForexData = function (obj) {
			$.ajax({
				url : "testing/moreData",
				data : obj,
				type : 'GET',
				contentType : "application/json",
				xhrFields: {
					  withCredentials: true
				}
			}).done(function(val) {
				console.log(val);
				if (val.content !== null) {
					var mainChart = $('#main-chart-forex');
					mainChart.empty();
					mainChart.append('<div class="flot-chart-content" id="main-chart-forex-content"></div>');
					createChartObject('main-chart-forex-content', mainChart.width(), mainChart.height(), val.dataList, val.indexList, false);
					var sp = val.content.split(";");
					gSystem.trend.push(parseInt(sp[2]));
					gSystem.actualTrend.push(parseInt(sp[3]));
					//gSystem.dates.push(getDateFromStr(gSystem.lastDate));
					gSystem.lastDate = sp[0];
					gSystem.finalDate = sp[1];
					console.log(gSystem);
					$('#span-mean').html(val.stat.mean);
					$('#span-sd').html(val.stat.sd);
					$('#span-new-mean').html(val.output.largerPercent);
					drawAccuracyChart(val.dataList);
					if (gSystem.running) {
						prepareGetMoreData();
					}
				}
			}).fail(function(val) {
				console.log(val);
			});
		};
		var getDateFromStr = function (str) {
			var year = parseInt(str.substring(0,4));
			var month = parseInt(str.substring(5,7));
			var day = parseInt(str.substring(8,10));
			var hour = parseInt(str.substring(11,13));
			var minute = parseInt(str.substring(14,16));
			var date = new Date(year, month, day, hour, minute, 0, 0);
			return date;
		};
		var drawAccuracyChart = function (dataList) {
			var n = dataList.length;
			var startIndex = n - gSystem.trend.length;
			var waveTrend = [];
			var numRight = 0;
			var numWrong = 0;
			for (var i = startIndex; i < n; i++) {
				var unit = {};
				unit.index = i;
				unit.type = (gSystem.trend[i-startIndex] === 2 ? 'buyTrend' : 'sellTrend');
				waveTrend.push(unit);
				if (i + 25 < n) {
					unit = {};
					unit.index = i;
					unit.type = (gSystem.actualTrend[i-startIndex] === 2 ? 'buyActual' : 'sellActual');
					waveTrend.push(unit);
					if (gSystem.trend[i-startIndex] === gSystem.actualTrend[i-startIndex]) {
						numRight++;
					} else {
						numWrong++;
					}
				}
			}
			if (numRight + numWrong > 0) {
				var percent = numRight / (numRight + numWrong) * 100;
				$('#div-forecasted-samples').html(numRight + '/' + numWrong + '(' + percent.toFixed(2) + '%)');
			}
			var accuracyChart = $('#accuracy-chart');
			accuracyChart.empty();
			accuracyChart.append('<div class="flot-chart-content" id="accuracy-chart-content"></div>');
			createChartObject('accuracy-chart-content', accuracyChart.width(), accuracyChart.height(), dataList, waveTrend, true);
		};
		
	};
	
	initPage();
	myInitPage();
});
var createChartObject = function (domObjStr, requiredWidth, requiredHeight, oriData, oriWave, isAccuracy) {
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
	
//	var coordsText = svg.append('text')
//	    .style("text-anchor", "end")
//	    .attr("class", "coords")
//	    .attr("x", width - 5)
//	    .attr("y", 15);
//	var enter = function () {
//		coordsText.style("display", "inline");
//	}
//	
//	var out = function () {
//		coordsText.style("display", "none");
//	}
//	
//	var move = function (coords) {
//		coordsText.text(
//		    timeAnnotation.format()(coords[0]) + ", " + ohlcAnnotation.format()(coords[1])
//		);
//	}
	
	var crosshair = techan.plot.crosshair()
	    .xScale(x)
	    .yScale(y)
	    .xAnnotation(timeAnnotation)//, timeTopAnnotation])
	    .yAnnotation(ohlcAnnotation);//, ohlcRightAnnotation])
//	    .on("enter", enter)
//	    .on("out", out)
//	    .on("move", move);
	
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
//	svg.append("rect")
//	    .attr("class", "pane")
//	    .attr("width", width)
//	    .attr("height", height)
//	    .call(zoom);
	
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
		
		if (isAccuracy === false) {
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
		} else {
			var trades = oriWave.map(function(unit) {
				var isMin = true;
				if (unit.type.startsWith('sell')) {
					isMin = false;
				}
				var isTrend = true;
				if (unit.type.endsWith('Actual')) {
					isTrend = false;
				}
				var offset = 0.00030;
				if (gSystem.name === 'TAIEX') {
					offset = 150;
				}
				return {
					date: data[unit.index].date, type: unit.type, 
					price: (isMin ? (isTrend ? data[unit.index].low : data[unit.index].low - offset) : (isTrend ? data[unit.index].high : data[unit.index].high + offset))
				};
			});
			svg.append("g")
			  .datum(trades)
			  .attr("class", "tradearrow")
			  .call(tradearrow);
		}
	}
//	var trades = [
//	              { date: data[0].date, type: "buy", price: data[0].low, quantity: 1000 },
//	              { date: data[1].date, type: "sell", price: data[1].high, quantity: 200 },
//	              { date: data[3].date, type: "buy", price: data[3].open, quantity: 500 },
//	              { date: data[5].date, type: "sell", price: data[5].close, quantity: 300 },
//	              { date: data[6].date, type: "buy-pending", price: data[6].low, quantity: 300 }
//	          ];
//  	svg.append("g")
//      .datum(trades)
//      .attr("class", "tradearrow")
//      .call(tradearrow);
	
	svg.select("g.candlestick").datum(data);
	var zoomable = x.zoomable();
	if (data.length > 200) {
		var mid = data.length / 2;
		var standard = 960 / (500 * 200);
		if (isAccuracy) {
			standard = standard * 2;
		}
		var real = width / height;
		var numberSampleShow = real / standard;
		zoomable.domain([data.length - numberSampleShow, data.length]);
	}
	
	draw();
	
	// Associate the zoom with the scale after a domain has been applied
	zoom.x(zoomable.clamp(false)).y(y);
};
var updateStatNumber = function(val) {
	//update Poles
	var inputPolePane = $('#input-wave-pole-pane');
//	var i = 2;
//	inputPolePane.find('li:nth-child(' + i + ')').find('.stat-percent').html('1.235');
//	inputPolePane.find('li:nth-child(' + i + ')').find('.progress-bar').css('width', '70%');
	for (var i = 0; i < 6; i++) {
		var j = i + 1;
		inputPolePane.find('li:nth-child(' + j + ')').find('.stat-percent').html(val.poles[i]);
		inputPolePane.find('li:nth-child(' + j + ')').find('.progress-bar').css('width', val.polePercents[i] + '%');
	}
	//update Distances
	var inputDistancePane = $('#input-wave-distance-pane');
	for (var i = 0; i < 5; i++) {
		var j = i + 1;
		inputDistancePane.find('li:nth-child(' + j + ')').find('.stat-percent').html(val.distances[i]);
		inputDistancePane.find('li:nth-child(' + j + ')').find('.progress-bar').css('width', val.distancePercents[i] + '%');
	}
	//update mean and sd
	inputDistancePane.find('.input-mean').html('Mean: ' + val.mean);
	inputDistancePane.find('.input-deviation').html('Standard deviation: ' + val.sd);
};
var updateOutputPercent = function(val) {
	var outputLargerSpan = $('#output-larger-span');
	outputLargerSpan.html(val.largerPercent + '%');
	outputLargerSpan.parent().find('.progress-bar').css('width', val.largerPercent + '%');
	var outputSmallerSpan = $('#output-smaller-span');
	outputSmallerSpan.html(val.smallerPercent + '%');
	outputSmallerSpan.parent().find('.progress-bar').css('width', val.smallerPercent + '%');
};
var drawErrorChart = function(data) {
	
	var barOptions = {
        series: {
            lines: {
                show: true,
                lineWidth: 2,
                fill: true,
                fillColor: {
                    colors: [{
                        opacity: 0.0
                    }, {
                        opacity: 0.0
                    }]
                }
            }
        },
        xaxis: {
            tickDecimals: 0
        },
        yaxis: {
            tickDecimals: 4
        },
        colors: ["#1ab394"],
        grid: {
            color: "#999999",
            hoverable: true,
            clickable: true,
            tickColor: "#D4D4D4",
            borderWidth:0
        },
        legend: {
            show: false
        },
        tooltip: true,
        tooltipOpts: {
            content: "x: %x, y: %y"
        }
    };
	
	var rData = [];
	for (var i = 0; i < data.length; i++) {
		rData.push([i+1, data[i]]);
	}
	
	var barData = {
        label: "bar",
        data: rData
	};
	
	
	
	
	var container = $('#mean-square-error-chart');
	$.plot(container, [barData], barOptions);
	
//	series = [{
//        data: rData,
//        lines: {
//            fill: true
//        }
//    }];
//	var plot = $.plot(container, series, {
//        grid: {
//            color: "#999999",
//            tickColor: "#D4D4D4",
//            borderWidth:0,
//            minBorderMargin: 20,
//            labelMargin: 10,
//            backgroundColor: {
//                colors: ["#ffffff", "#ffffff"]
//            },
//            margin: {
//                top: 8,
//                bottom: 20,
//                left: 20
//            }
//        },
//        colors: ["#1ab394"],
//        xaxis: {
//            tickFormatter: function() {
//                return "";
//            }
//        },
//        yaxis: {
//            min: 0,
//            max: 0.1
//        }
//    });
};