package tjamtjam.monthlywages.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import tjamtjam.monthlywages.exception.InvalidShiftListException;
import tjamtjam.monthlywages.exception.InvalidShiftRecordException;
import tjamtjam.monthlywages.model.MonthlyWages;
import tjamtjam.monthlywages.model.WageSettings;
import tjamtjam.monthlywages.service.WageService;

@Controller
public class WageController {

	private static final Logger log = LoggerFactory.getLogger(WageController.class);
	
	@Autowired
	private WageService wageService;
	
	@RequestMapping(method=RequestMethod.GET)
	public String index() {
		return "page";
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public String computeWages(Model model, @RequestParam("shiftList") MultipartFile shiftList) {
		if (shiftList.isEmpty()) {
			model.addAttribute("error", "Please choose a non empty file!");
			return "page";
		}
		if (shiftList.getSize() > 8L*1048576) {
			model.addAttribute("error", "Too large file!");
			return "page";
		}
		WageSettings settings = WageSettings.DEFAULT;
		model.addAttribute("settings", settings);
		MonthlyWages wages;
		try {
			wages = this.wageService.computeMonthlyWages(shiftList.getInputStream(), settings);
			model.addAttribute("wages", wages);
		}
		catch (IOException e) {
			log.error("Failed to read shift list!", e);
			model.addAttribute("error", "Failed to read the selected file.");
		}
		catch (InvalidShiftRecordException e) {
			log.error("Invalid record in shift list!", e);
			switch (e.getType()) {
			case INVALID_DATE:
				model.addAttribute("error", String.format("Invalid date '%1$s' at record %2$s.", e.getData(), e.getRecordNumber()));
				break;
			case INVALID_END_TIME:
				model.addAttribute("error", String.format("Invalid end time '%1$s' at record %2$s.", e.getData(), e.getRecordNumber()));
				break;
			case INVALID_START_TIME:
				model.addAttribute("error", String.format("Invalid start time '%1$s' at record %2$s.", e.getData(), e.getRecordNumber()));
				break;
			case MISSING_PERSON_ID:
				model.addAttribute("error", String.format("Person ID missing from record %1$s.", e.getRecordNumber()));
				break;
			case MISSING_PERSON_NAME:
				model.addAttribute("error", String.format("Person name missing from record %1$s.", e.getRecordNumber()));
				break;
			case WRONG_NUMBER_OF_DATA_ELEMENTS:
				model.addAttribute("error", String.format("Wrong number (%1$s) of data elements at record %2$s.", e.getData(), e.getRecordNumber()));
				break;
			default:
				break;
			}
		}
		catch (InvalidShiftListException e) {
			log.error("Invalid data from shift list!", e);
			model.addAttribute("error", "The selected shift list is invalid. Does it contain any records?");
		}
		return "page";
	}
}
