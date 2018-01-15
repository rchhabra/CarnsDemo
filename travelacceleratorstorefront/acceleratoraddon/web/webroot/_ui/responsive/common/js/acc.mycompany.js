/**
 * The module for my company pages.
 * @namespace
 */
 ACC.mycompany = {

     _autoloadTracc : [ 
        'bindCompanySelectors',
        'bindBudgetDatesValidation',
        'bindPermissionOptions',
        'bindPaymentCostCentreHide'
    ],
    
    componentParentSelector: '.y_budgetDates',

    /**
     * validation for budget dates form
     */
    bindBudgetDatesValidation : function() {
        $(ACC.mycompany.componentParentSelector).validate({
            errorElement: 'span',
            errorClass: 'fe-error',
            ignore: '.fe-dont-validate',
            submitHandler: function() { window.location.href = 'MyCompany-AddBudgets.html, MyCompany-EditBudgets.html' },
            onfocusout: function(element) { $(element).valid(); },
            rules: {
                datePickerStart: 'required',
                datePickerEnd: 'required'
            },
            messages: {
                datePickerStart: 'Please select a start date.',
                datePickerEnd: 'Please select an end date.'
            }
        });


        $('#datePickerStart').datepicker({ 
            onClose: function( selectedDate ) {
                $( '#datePickerEnd' ).datepicker( 'option', 'minDate', selectedDate );
                $(this).valid();
          }
        });
        $('#datePickerEnd').datepicker({ 
            onClose: function( selectedDate ) {
                $(this).valid();
          }
        });
        
    },
    
    /**
    * show approver selected/deselected state (used on My Company - Select Approvers page)
    */
    bindCompanySelectors : function() {
        $('.y_companySelectors button').each(function(index) {
            $(this).on('click', function() {
                $(this).parents('.card').toggleClass('selected');
                $(this).text($(this).text() == 'Deselect' ? 'Select' : 'Deselect');
            });
        });
        
    },
    
    /**
    * Hide/show permission form elements based on the Permission Type chosen
    */
    bindPermissionOptions : function() {
        var selectNewPermissionType = '#y_selectNewPermissionType';
        $(selectNewPermissionType).change(function(){
            
            var perTimespan = $('option:eq(1)', selectNewPermissionType).val(),
            perOrder = $('option:eq(2)', selectNewPermissionType).val(),
            perExceeded = $('option:eq(3)', selectNewPermissionType).val(),
            formGroup = '.form-group',
            timePeriod = '#y_timePeriod',
            exceededOptionsHide = '#y_timePeriod, #y_thresholdAmount, #y_permCurrency',
            perOrderOptionsHide = '#y_thresholdAmount, #y_permCurrency';
            
            if($(this).val() == perTimespan) {
                $('.y_permissionForm .form-group:hidden').show('fast');
            } else if($(this).val() == perOrder) {
                $(timePeriod).closest(formGroup).hide('fast');
                
                if($(perOrderOptionsHide).closest(formGroup).is(':hidden')){
                    $(perOrderOptionsHide).closest(formGroup).show('fast');
                }
                
            } else if($(this).val() == perExceeded) {
                $(exceededOptionsHide).closest(formGroup).hide('fast');
            }
        });
    },

    /**
    * Hide/show cost centre drop-down depending on the payment method
    */
    bindPaymentCostCentreHide: function () {
        
        var costCentre = '#y_pdCostCentre';
        
        $('#y_paymentType1').click(function() {
          $(costCentre).parent().hide('fast');
        });
        $('#y_paymentType2').click(function() {
            if ($(costCentre).parent().is(':hidden')){
                $(costCentre).parent().show('fast');
            }
        });
    },
    
    
    
}