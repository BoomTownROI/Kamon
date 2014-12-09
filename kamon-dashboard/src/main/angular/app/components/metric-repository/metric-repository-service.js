angular.module('kamonDashboard')
  .factory('MetricRepository', ['EventStream', 'EventSubscriptions', 'HistogramSnapshot', '$log', function(eventStream, eventSubscriptions, histogramSnapshot, $log) {
    var metricRepository = {};
    var allEntityDetails = [];
    var entitiesMetrics = [];
    var metricsChangeListeners = eventSubscriptions.create();

    function _metricID(name, category) {
      return {
        hash: md5('' + name + category),
        name: name,
        category: category
      };
    }

    function _pushSnapshot(entityID, snapshot) {
      var entity = _.find(entitiesMetrics, function(em) { return em.details.hash === entityID.hash; });

      if(entity !== undefined) {
        entity.snapshots.push(snapshot);
        metricsChangeListeners.fireEvent(entityID.hash, snapshot);

      } else {
        // If the entity doesn't exist, register it.
        allEntityDetails.push(entityID);
        entitiesMetrics.push({
          details: entityID,
          snapshots: []
        })
      }
    }

    function _convertMetricToObject(rawMetric) {
      if(rawMetric.type === 'histogram') {
        return histogramSnapshot.create(rawMetric.recordings);
      } else if(rawMetric.type === 'counter') {
        return { count: rawMetric.value };
      } else {
        return undefined;
      }
    }
   

    eventStream.subscribe('metrics', function(metricsBatch) {
      _.each(metricsBatch.entities, function(entity) {
        var entityID = _metricID(entity.name, entity.category);
        var metricNames = _(entity.metrics).keys();
        var metricValues = _(entity.metrics).values().map(_convertMetricToObject);
        var metrics = _.object(metricNames, metricValues);

        var snapshot = {
          from: metricsBatch.from,
          to: metricsBatch.to,
          metrics: metrics
        };

        _pushSnapshot(entityID, snapshot);
      });

      metricsChangeListeners.fireEvent('new-batch');
    });

    metricRepository.allEntities = function() {
      return allEntityDetails;
    };

    metricRepository.entityMetrics = function(hash) {
      return _.find(entitiesMetrics, function(em) { return em.details.hash === hash; });
    }

    metricRepository.addMetricBatchArriveListener = function(callback) {
      return metricsChangeListeners.subscribe('new-batch', callback);
    };

    metricRepository.addEntityMetricsListener = function(hash, callback) {
      return metricsChangeListeners.subscribe(hash, callback);
    };
    
    return metricRepository;
  }]);