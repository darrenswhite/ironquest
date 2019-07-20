var QuestsPathForm = (function () {
  var RS_WIKI_URL = 'https://runescape.wiki/';
  var $actions;
  var $pathStats;
  var $run;
  var $questsPathForm;
  var $lampSkills;

  function init() {
    initSelectors();

    $questsPathForm.on('submit', function (event) {
      setLoading(true);
      event.preventDefault();

      $.get({
        url: 'api/quests/path',
        data: $questsPathForm.serialize(),
        success: displayActionsSuccess,
        error: displayActionsError
      });
    });

    $lampSkills.multiSelect({
      keepOrder: true,
      afterSelect: function (value) {
        $lampSkills.find('option[value="' + value + '"]').remove();
        $lampSkills.append($("<option></option>").attr('value', value).attr('selected', 'selected'));
      },
      afterDeselect: function (value) {
        $lampSkills.find('option[value="' + value + '"]').removeAttr('selected');
      }
    });
  }

  function initSelectors() {
    $actions = $('#actions');
    $pathStats = $('#path-stats');
    $run = $('#run');
    $questsPathForm = $('#quests-path-form');
    $lampSkills = $('#lamp-skills');
  }

  function displayActionsSuccess(path) {
    setLoading(false);

    var pathStats = getPathStats(path);
    var actionsContent = getActionsContent(path);

    $pathStats.html(pathStats);
    $actions.html(actionsContent);
  }

  function getPathStats(path) {
    var stats = path.stats;

    return '<div class="col-12"><span class="badge badge-pill badge-success">Completed: <span class="badge badge-pill badge-light">' +
      Math.round(stats.percentComplete) + '%</span></span></div>';
  }

  function getActionsContent(path) {
    var actions = path.actions;
    var actionsContent;

    if (actions.length > 0) {
      var tabs = '<div class="col-6"><div class="list-group list-group-flush" role="tablist" id="actions-list">';
      var tabContent = '<div class="col-6"><div class="tab-content">';
      var actionId;
      var actionContent;

      path.actions.forEach(function (action, i) {
        actionId = 'action-' + i;
        actionContent = getActionContent(action);

        tabs += '<a class="list-group-item list-group-item-action' + (i == 0 ? ' active' : '') +
          '" data-toggle="list" href="#' + actionId + '" role="tab">' + action.message + '</a>';
        tabContent += '<div class="tab-pane fade ' + (i == 0 ? ' show active' : '') +
          '" id="' + actionId + '" role="tabpanel">' + actionContent + '</div>';
      });

      tabs += '</div></div>';
      tabContent += '</div></div>';
      actionsContent = tabs + tabContent;
    } else {
      actionsContent = '<div class="col-12"><p id="actions-empty"><em>None</em></p></div>';
    }

    return actionsContent;
  }

  function getActionContent(action) {
    var content = '<div class="action-content">';

    if (typeof action.quest !== 'undefined') {
      var questWikiUrl = RS_WIKI_URL + action.quest.displayName.replace(/ /g, '_');

      content += '<div><a href="' + questWikiUrl + '" target="_blank">View quest on wiki</a><div>';
    }

    content += '<table><tbody>';
    content += '<tr><td>Combat level:</td><td>' + action.player.combatLevel + '</td></tr>';
    content += '<tr><td>Quest points:</td><td>' + action.player.questPoints + '</td></tr>';
    content += '<tr><td>Total level:</td><td>' + action.player.totalLevel + '</td></tr>';

    Object.keys(action.player.levels).sort().forEach(function (skill) {
      content += '<tr><td>' + toTitleCase(skill) + ':</td><td>' + action.player.levels[skill] + '</td></tr>';
    });

    content += '</table></tbody></div>';

    return content;
  }

  function toTitleCase(value) {
    return value[0].toUpperCase() + value.substring(1).toLowerCase();
  }

  function displayActionsError(response) {
    setLoading(false);
    $actions.html('<div class="col-12"><p id="action-error">Something went wrong!</p></div>');
    $pathStats.html('');
  }

  function setLoading(loading) {
    if (loading) {
      $run.prop('disabled', true);
      $run.html('<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true">' +
        '</span><span class="sr-only">Loading...</span>');
    } else {
      $run.prop('disabled', false);
      $run.html('Run');
    }
  }

  function load() {
    var cache = localStorage.questsPathForm;

    if (cache) {
      cache = JSON.parse(cache);

      cache.forEach(function (field) {
        var $input = $('[name="' + field.name + '"]');
        var value = field.value;

        setInputValue($input, value);
      });
    }
  }

  function setInputValue($input, value) {
    if ($input.length === 1) {
      var inputType = $input.prop('type') || 'text';

      if (inputType === 'text') {
        $input.val(value);
      } else if (inputType === 'checkbox') {
        $input.prop('checked', value === 'on');
      } else if (inputType === 'select-multiple') {
        $input.multiSelect('select', value);
      }
    } else if ($input.length > 1) {
      $input.filter('[value="' + value + '"]').prop('checked', true);
    }
  }

  function save() {
    localStorage.questsPathForm = JSON.stringify($questsPathForm.serializeArray());
  }

  return {
    init: init,
    load: load,
    save: save
  };
})();

$(function() {
  QuestsPathForm.init();
  QuestsPathForm.load();
  $(window).on('beforeunload', QuestsPathForm.save);

  $('[data-toggle="popover"]').popover({
    trigger: 'hover'
  });
});
