$(".b-form-input_dropdown").click(function () {
	$(this).toggleClass("is-active");
});

$(".b-form-input-dropdown-list").click(function(e) {
	e.stopPropagation();
});

$('.js-scrollbar').perfectScrollbar({
	"wheelPropagation": true,
	"swipePropagation": false
});

$('body').on('click', '.js-hide-resources', function(){
	$('.b-cloud-add-tools').animate({'left': 0}, 300);
	return false;
});

$(document).on('click', '.b-nav-menu-link', function () {
	if ($(this).next('.b-nav-menu__sub').length > 0) {
		if ($(this).parent().hasClass('b-nav-menu_active')) {
			$(this).parent().removeClass('b-nav-menu_active');
			$(this).next('.b-nav-menu__sub').stop().slideUp(300);
		} else {
			$('.b-nav-menu_active .b-nav-menu__sub').parent().removeClass('b-nav-menu_active')
			$('.b-nav-menu__sub').stop().slideUp(300);
			$(this).parent().addClass('b-nav-menu_active');
			$(this).next('.b-nav-menu__sub').stop().slideDown(300);
		}
		return false;
	} else {
		if($(this).parent().hasClass('b-nav-menu_active')) {
			location.reload();
		} else {
			$('.b-nav-menu__sub').stop().slideUp(300);
			$('.b-nav-menu_active').removeClass('b-nav-menu_active');
		}
	}
});

$('body').on('click', '.js-notification', function() {
	$('.b-hub-status__dropdown').stop().slideUp(100);
	var currentDropDown = $(this).next('.b-hub-status__dropdown');
	if(currentDropDown.hasClass('b-hub-status__dropdown_open')) {
		$('.b-hub-status__dropdown_open').removeClass('b-hub-status__dropdown_open');
	} else {
		$('.b-hub-status__dropdown_open').removeClass('b-hub-status__dropdown_open');
		currentDropDown.stop().slideDown(200);
		currentDropDown.addClass('b-hub-status__dropdown_open');
	}
	return false;
});

$(document).on('click', function(event) {
	if(!$(event.target).closest('.js-header-dropdown').hasClass('js-header-dropdown')){
		$('.b-hub-status__dropdown').stop().slideUp(100);
		$('.b-hub-status__dropdown_open').removeClass('b-hub-status__dropdown_open');
	}

	if(
		!$(event.target).closest('.js-no-close').hasClass('js-no-close') &&
		$(event.target).closest('g').attr('class') != 'element-call-menu' &&
		$(event.target).closest('g').attr('class') != 'b-container-plus-icon'
	){
		$('.b-template-settings').stop().hide(100);
	}
});

$(document).keyup(function(e) {
	if (e.keyCode == 27) {
		$('.b-hub-status__dropdown').stop().slideUp(100);
		$('.b-hub-status__dropdown_open').removeClass('b-hub-status__dropdown_open');
		$('.ssh-plugin-info-tooltip').hide();
	}
});

$('body').click(function (event) {
	if (
		!$(event.target).hasClass('ssh-info-button') &&
		!$(event.target).parent().hasClass("ssh-plugin-info-tooltip") &&
		!$(event.target).hasClass("ssh-plugin-info-tooltip")
	) {
		$('.ssh-plugin-info-tooltip').hide();
	}
});

$(document).on('click', '.ssh-info-button', function (event) {
	if($(event.target).hasClass("ssh-info-button")) {
		var status = $(event.target).find(".ssh-plugin-info-tooltip").css('display') == "none";
		if(status) {
			$('.ssh-plugin-info-tooltip').hide();
			$(event.target).find('.ssh-plugin-info-tooltip').toggle();
		} else {
			$(event.target).find('.ssh-plugin-info-tooltip').toggle();
		}
	}
});

function accordionInit() {
	// Accordion
	$('.accordion').each(function (index, el) {
		var $that = $(this);
		var $items = $(this).find('.accordion__item');
		var $headers = $(this).find('.accordion__header');
		var $contents = $(this).find('.accordion__content');
		var speed = 300;

		$items.each(function (index, el) {
			var findActive = false;

			if (!findActive) {
				if ($(this).hasClass('accordion__item_active')) {
					$(this).children('.accordion__content').show();
					return false;
				}
			}
			else {
				$(this).removeClass('accordion__item_active');
			}
		});

		$headers.click(function (event) {
			event.preventDefault();

			var $item = $(this).parent();

			if (!$item.hasClass('accordion__item_active')) {
				$items.removeClass('accordion__item_active');
				$item.addClass('accordion__item_active');
				$contents.slideUp(speed);
				$item.children('.accordion__content').slideDown(speed);
			}
			else {
				$item.children('.accordion__content').slideUp(speed);
				$item.removeClass('accordion__item_active');
			}
		});
	});
}

var UPDATE_NIGHTLY_BUILD_STATUS;

