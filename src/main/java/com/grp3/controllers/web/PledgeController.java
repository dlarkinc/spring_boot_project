package com.grp3.controllers.web;

import java.util.Locale;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.grp3.control.forms.PledgeForm;
import com.grp3.logic.factories.IFormsToModelsFactory;
import com.grp3.models.Pledge;
import com.grp3.services.IPledgeService;
import com.grp3.services.IProjectService;
import com.grp3.services.IUserSecurityService;

@Controller
@RequestMapping("/pledges")
public class PledgeController {
	
	@Autowired
	private IPledgeService _pledgeService;
	
	@Autowired
	private IProjectService _projService;
	
	@Autowired
	private IUserSecurityService _userSecService;
	
	@Autowired
	private IFormsToModelsFactory _formsToModelsFactory;

	@RequestMapping(value="/create", method = RequestMethod.POST)
	public String createPledge(@ModelAttribute(IControllerConstants.PLEDGE_FORM) final @Valid PledgeForm pledgeForm, BindingResult bindingResult, final RedirectAttributes redirectAttributes, Locale locale, Model model) {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(IControllerConstants.REDIRECT);
		strBuilder.append(IControllerConstants.PROJECT_ROUTE);
		strBuilder.append(IControllerConstants.FW_SLASH);
		strBuilder.append(pledgeForm.getProjectId());
		if(bindingResult.hasErrors()) {
			redirectAttributes.addFlashAttribute(IControllerConstants.BINDINGRESULT_PACKAGE + IControllerConstants.PLEDGE_FORM, bindingResult);
			redirectAttributes.addFlashAttribute(IControllerConstants.PLEDGE_FORM, pledgeForm);
			strBuilder.append(IControllerConstants.QMARK);
			strBuilder.append(IControllerConstants.ERRORS_PARAM);
		} else {
			Pledge pledge = _formsToModelsFactory.pledgeFormToPledge(pledgeForm);
			pledge.setProject(_projService.getProject(pledgeForm.getProjectId()));
			pledge.setUser(_userSecService.getLoggedInUser());
			_pledgeService.save(pledge);
		}
		return strBuilder.toString();
	}
}
