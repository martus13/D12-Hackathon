
package controllers.user;

import java.util.Collection;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.AirportService;
import services.CreditCardService;
import services.ExchangeRateService;
import services.FinderService;
import services.FlightService;
import services.UserService;
import controllers.AbstractController;
import domain.Airport;
import domain.CreditCard;
import domain.ExchangeRate;
import domain.Finder;
import domain.User;

@Controller
@RequestMapping("/finder/user")
public class FinderUserController extends AbstractController {

	// Services ---------------------------------------------------------------
	@Autowired
	private FinderService		finderService;

	@Autowired
	private UserService			userService;

	@Autowired
	private CreditCardService	creditCardService;

	@Autowired
	private AirportService		airportService;

	@Autowired
	private FlightService		flightService;

	@Autowired
	private ExchangeRateService	exchangeRateService;


	// Constructors -----------------------------------------------------------

	public FinderUserController() {
		super();
	}

	// Display ----------------------------------------------------------------		
	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public ModelAndView display() {
		ModelAndView result;
		User user;
		Finder finder;

		user = this.userService.findByPrincipal();

		finder = this.finderService.findByUserId(user.getId());

		result = new ModelAndView("finder/display");
		result.addObject("finder", finder);
		return result;
	}

	// Creation ---------------------------------------------------------------		
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create() {
		ModelAndView result;
		Finder finder;

		finder = this.finderService.create();

		result = this.createEditModelAndView(finder);

		return result;
	}

	@RequestMapping(value = "/findByFinder", method = RequestMethod.GET)
	public ModelAndView findByFinder(@RequestParam final int finderId) {
		ModelAndView result;
		Finder finder;
		CreditCard creditCard;
		User user;
		Collection<Object[]> flights;
		Collection<ExchangeRate> exchangeRates;
		Boolean checkCreditCard;

		user = this.userService.findByPrincipal();
		Assert.notNull(user);

		finder = this.finderService.findOne(finderId);
		Assert.notNull(finder);
		Assert.isTrue(finder.getUser().equals(user));

		creditCard = this.creditCardService.findByUser(user.getId());

		flights = this.flightService.findByFinder(finder);
		exchangeRates = this.exchangeRateService.findAll();
		checkCreditCard = this.creditCardService.checkValidation(creditCard);

		result = new ModelAndView("finder/search");
		result.addObject("finder", finder);
		result.addObject("flights", flights);
		result.addObject("exchangeRates", exchangeRates);
		result.addObject("checkCreditCard", checkCreditCard);
		result.addObject("requestURI", "finder/user/findByFinder.do?finderId=" + finderId);

		return result;
	}

	// Edition ----------------------------------------------------------------		
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam final int finderId) {
		ModelAndView result;
		Finder finder;

		finder = this.finderService.findOne(finderId);
		Assert.notNull(finder);

		result = this.createEditModelAndView(finder);

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@Valid final Finder finder, final BindingResult binding) {
		ModelAndView result;

		if (binding.hasErrors()) {
			System.out.println(binding.toString());
			result = this.createEditModelAndView(finder);

		} else
			try {
				this.finderService.save(finder);
				result = new ModelAndView("redirect:display.do");

			} catch (final Throwable oops) {
				System.out.println(oops.toString());
				result = this.createEditModelAndView(finder, "finder.commit.error");
			}
		return result;
	}

	// Deleting ---------------------------------------------------------------		
	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "delete")
	public ModelAndView delete(@Valid final Finder finder, final BindingResult binding) {
		ModelAndView result;

		this.finderService.delete(finder);
		result = new ModelAndView("redirect:display.do");

		return result;
	}

	// Ancillary methods ------------------------------------------------------		
	protected ModelAndView createEditModelAndView(final Finder finder) {
		ModelAndView result;

		result = this.createEditModelAndView(finder, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final Finder finder, final String message) {
		ModelAndView result;
		Collection<Airport> airports;

		airports = this.airportService.findNotDeleted();

		result = new ModelAndView("finder/edit");
		result.addObject("finder", finder);
		result.addObject("airports", airports);
		result.addObject("message", message);

		return result;
	}
}
