var RS_WIKI_URL = 'https://runescape.wiki/'

function displayActionsSuccess(response) {
  setLoading(false);

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

  $('#actions').html(tabs + tabContent);
}

function getActionContent(action) {
  var content = '<p class="action-content">';

  if (typeof action.questEntry !== "undefined") {
    var questWikiUrl = RS_WIKI_URL + action.questEntry.quest.displayName.replace(/ /g, '_');

    content += '<a href="' + questWikiUrl + '">View quest on wiki</a>\n';
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
  return value[0].toUpperCase() + value.substring(1).toLowerCase()
}

function displayActionsError(response) {
  setLoading(false);
  $('#actions').html('<div class="col-12"><p id="action-error">Something went wrong!</p></div>');
}

function setLoading(loading) {
  var $run = $('#run');

  if (loading) {
    $run.prop('disabled', true);
    $run.html('<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true">' +
      '</span><span class="sr-only">Loading...</span>');
  } else {
    $run.prop('disabled', false);
    $run.html('Run');
  }
}

$(function() {
  $('#quests-path-form').on('submit', function(e) {
    setLoading(true);
    e.preventDefault();

    $.get({
      url: 'api/quests/path',
      data: $(this).serialize(),
      success: displayActionsSuccess,
      error: displayActionsError
    });
  });
  $('#lamp-skills').multiSelect({
    keepOrder: true
  });
});
