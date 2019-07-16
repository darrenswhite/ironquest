var QuestsPathForm = function () {
  var RS_WIKI_URL = 'https://runescape.wiki/';
  var $actions = $('#actions');
  var $run = $('#run');
  var $questsPathForm = $('#quests-path-form');
  var $lampSkills = $('#lamp-skills');

  function displayActionsSuccess(response) {
    setLoading(false);

    var actions = response.actions;
    var actionsContent;

    if (actions.length > 0) {
      var tabs = '<div class="col-6"><div class="list-group list-group-flush" role="tablist" id="actions-list">';
      var tabContent = '<div class="col-6"><div class="tab-content">';
      var actionId;
      var actionContent;

      response.actions.forEach(function (action, i) {
        actionId = 'action-' + i;
        actionContent = getActionContent(action);

        tabs += '<a class="list-group-item list-group-item-action" data-toggle="list" href="#' +
          actionId + '" role="tab">' + action.message + '</a>';
        tabContent += '<div class="tab-pane fade" id="' + actionId + '" role="tabpanel">' +
          actionContent + '</div>';
      });

      tabs += '</div></div>';
      tabContent += '</div></div>';
      actionsContent = tabs + tabContent;
    } else {
      actionsContent = '<div class="col-12"><p id="actions-empty"><em>None</em></p></div>';
    }

    $actions.html(actionsContent);
  }

  function getActionContent(action) {
    var content = '<p class="action-content">';

    if (typeof action.quest !== 'undefined') {
      var questWikiUrl = RS_WIKI_URL + action.quest.displayName.replace(/ /g, '_');

      content += '<a href="' + questWikiUrl + '" target="_blank">View quest on wiki</a>\n';
    }

    content += 'Combat level: ' + action.player.combatLevel + '\n';
    content += 'Quest points: ' + action.player.questPoints + '\n';
    content += 'Total level: ' + action.player.totalLevel + '\n';

    Object.keys(action.player.levels).sort().forEach(function (skill) {
      content += toTitleCase(skill) + ': ' + action.player.levels[skill] + '\n';
    });

    content += '</p>';

    return content;
  }

  function toTitleCase(value) {
    return value[0].toUpperCase() + value.substring(1).toLowerCase();
  }

  function displayActionsError(response) {
    setLoading(false);
    $actions.html('<div class="col-12"><p id="action-error">Something went wrong!</p></div>');
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

  function init() {
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
};

$(function() {
  var questsPathForm = new QuestsPathForm();
  questsPathForm.init();
  questsPathForm.load();
  $(window).on('beforeunload', questsPathForm.save);
});
