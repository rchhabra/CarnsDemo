ACC.travelcommerceorg = {

    _autoload: [
        "bindToSelectBudget",
        "bindToDeselectBudget",
        "bindToSelectLink",
        "bindToDeselectLink",
        "bindToActionConfirmationModalWindow",
        "disablePermissionConfirmation",
        "bindToSelectUser",
        "bindToDeselectUser",
        "bindToUnitAddUserButton",
        "disablePermissionConfirmation",
        "bindPermissionTypeSelectionForAddNew",
        "bindToRemoveUserFromUnit",
        "bindCompanySelectors",
        "bindBudgetDatesValidation",
        "bindDatePickerOnOrders"
    ],

    bindToSelectBudget: function()
    {
        $(document).on('click','.js-selectBudget',function(){
            var url = $(this).attr('url');
            $.postJSON(url,{}, ACC.travelcommerceorg.selectionCallback);
            return false;
        });

    },

    bindToDeselectBudget: function()
    {
        $(document).on('click','.js-deselectBudget',function(){
            var url = $(this).attr('url');
            $.postJSON(url,{}, ACC.travelcommerceorg.deselectionCallback);
            return false;
        });
    },

    selectionCallback: function(budget)
    {
        $('#card-' + budget.normalizedCode).addClass("selected");
        $('#span-' + budget.normalizedCode).html($('#enableDisableLinksTemplate').tmpl(budget));
    },

    deselectionCallback: function(budget)
    {
        $('#card-' + budget.normalizedCode).removeClass("selected");
        $('#span-' + budget.normalizedCode).html($('#enableDisableLinksTemplate').tmpl(budget));
    },

    disablePermissionConfirmation: function(data)
    {
        $(document).on("click",".js-disable-permission-confirmation",function(e){
            e.preventDefault();

            ACC.colorbox.open("",{
                inline:true,
                href: "#disablePermission",
                width:"620px",
                onComplete: function(){
                    $(this).colorbox.resize();
                }
            });
        });

        $(document).on("click",'#disablePermission #cancelDisablePermission', function (e) {
            e.preventDefault();
            $.colorbox.close();
        });
    },

    bindPermissionTypeSelectionForAddNew: function ()
    {
        $('#selectNewPermissionType').on("change", function (e)
        {
            $.ajax({
                url: ACC.config.encodedContextPath + '/my-company/organization-management/manage-permissions/getNewPermissionForm',
                async: true,
                data: {'permissionType':$(this).val()},
                dataType: "html",
                beforeSend: function ()
                {
                    $("#addNewPermissionForm").html(ACC.address.spinner);
                }
            }).done(function (data)
            {
                $("#addNewPermissionForm").html($(data).html());
                ACC.travelcommerceorg.bindPermissionTypeSelectionForAddNew();
            });
        })
    },

    bindToSelectUser: function()
    {
        $(document).on('click','.y_selectUser',function(){
            var url = $(this).attr('url');
            $.postJSON(url,{}, ACC.travelcommerceorg.userSelectionCallback);
            return false;
        });
    },

    bindToDeselectUser: function()
    {
        $(document).on('click','.y_deselectUser',function(){
            var url = $(this).attr('url');
            $.postJSON(url,{}, ACC.travelcommerceorg.userSelectionCallback);
            return false;
        });
    },

    bindToRemoveUserFromUnit: function()
    {
        $(document).on('click','.y_removeUserItem',function(){
            var removeUserFromUnit = $(this).parents('.card');
            var counterElem = $(this).parents('.account-list').find('.y_userCounter');

            $.postJSON(this.getAttribute('url'), {}, function(){
                removeUserFromUnit.remove();
                counterElem.text(counterElem.text() - 1);
            });

            return false;
        });
    },
    
    bindCompanySelectors : function() {
    	$('.y_company-selectors button').each(function(index) {
    		$(this).on('click', function() {
            	$(this).parents('.card').toggleClass('selected');
            	$(this).text($(this).text() == 'Deselect' ? 'Select' : 'Deselect');
            });
    	});
        
    },

    userSelectionCallback: function(user)
    {
        var userNormalizedId = typeof user.normalizedUid != 'undefined' ? user.normalizedUid : user.normalizedCode;

        $('#selection-' + userNormalizedId).html($('#enableDisableLinksTemplate').tmpl(user));
        $('#roles-' + userNormalizedId).html($('#userRolesTemplate').tmpl(user));
        if (user.selected)
        {
            $('#row-' + userNormalizedId).addClass("selected");
        }
        else
        {
            $('#row-' + userNormalizedId).removeClass("selected");
        }
    },

    bindToSelectLink: function()
    {
        $(document).on('click','.y_selectLink',function(){
            var url = $(this).attr('url');
            $.postJSON(url,{}, ACC.travelcommerceorg.selectionCallbackLink);
            return false;
        });
    },

    bindToDeselectLink: function()
    {
        $(document).on('click','.y_deselectLink',function(){
            var url = $(this).attr('url');
            $.postJSON(url,{}, ACC.travelcommerceorg.deselectionCallbackLink);
            return false;
        });
    },

    selectionCallbackLink: function(permission)
    {
        $('#row-' + permission.normalizedCode).addClass("selected");
        $('#span-' + permission.normalizedCode).html($('#enableDisableLinksTemplate').tmpl(permission));
    },

    deselectionCallbackLink: function(permission)
    {
        $('#row-' + permission.normalizedCode).removeClass("selected");
        $('#span-' + permission.normalizedCode).html($('#enableDisableLinksTemplate').tmpl(permission));
    },


    bindToActionConfirmationModalWindow: function()
    {
        $('.js-action-confirmation-modal a').click(function(e){
            e.preventDefault();

            var title = $(this).data('action-confirmation-modal-title');
            var id = $(this).data('action-confirmation-modal-id');
            var modalWindow = $('#js-action-confirmation-modal-content-' + id);

            if (modalWindow.data('useSourceElementUrl') === true) {
                var url = $(this).prop('href');

                modalWindow.find('.url-holder').each(function(index, element) {
                    var target = $(element);

                    if (target.is("form")) {
                        target.prop('action', url);
                    } else {
                        target.prop('href', url);
                    }
                });
            }

            ACC.colorbox.open(title,{
                inline:true,
                href:modalWindow,
                width:"480px",
                onComplete: function(){
                    ACC.colorbox.resize();
                }
            });
        });

        $('.js-action-confirmation-modal-cancel').click(function(e){
            e.preventDefault();
            ACC.colorbox.close();
        });
    },

    bindToUnitAddUserButton: function()
    {
        $('.js-add-user-action').click(function(e) {
            $(this).parent('.add-user-action-menu').toggleClass('open');
            return false;
        });
    },
    
    bindBudgetDatesValidation : function() {
    	if(!$('.y_b2bBudgetform').is('form')){
    		return;
    	}
		$('.y_b2bBudgetform').validate({
            errorElement: 'span',
            errorClass: 'fe-error',
            ignore: '.fe-dont-validate',
            onfocusout: function(element) { $(element).valid(); },
            rules: {
				dateUK : true,
            	startDate: 'required',
            	endDate: 'required'
            },
            messages: {
            	startDate: 'Please select a start date.',
            	endDate: 'Please select an end date.'
            }
        });
		
        $(".y_datePickerStart").datepicker({ 
            onClose: function( selectedDate ) {
                $(".y_datePickerEnd").datepicker( 'option', 'minDate', selectedDate );
                $(".y_datePickerStart").rules("add",{
					dateUK : true,
					messages:{
		            	dateUK: ACC.addons.travelacceleratorstorefront['error.formvalidation.dateUK']
					}
				});
                $(this).valid();
          }
        });
        
        $(".y_datePickerEnd").datepicker({ 
            onClose: function( selectedDate ) {
                $(".y_datePickerEnd").rules("add",{
					dateUK : true,
					messages:{
		            	dateUK: ACC.addons.travelacceleratorstorefront['error.formvalidation.dateUK']
					}
				});
                $(this).valid();
          }
        });		
	},
    
    bindDatePickerOnOrders : function(){
        $(".y_ordersFromDate").datepicker({ 
            onClose: function( selectedDate ) {
                $(this).valid();
          }
        });
        
        $(".y_ordersToDate").datepicker({ 
            onClose: function( selectedDate ) {
                $(this).valid();
          }
        });	
    }
};
