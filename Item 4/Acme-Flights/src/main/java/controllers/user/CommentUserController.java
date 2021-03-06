
package controllers.user;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.AirlineService;
import services.CommentService;
import controllers.AbstractController;
import domain.Airline;
import domain.Comment;

@Controller
@RequestMapping("/comment/user")
public class CommentUserController extends AbstractController {

	// Services ---------------------------------------------------------------

	@Autowired
	private CommentService	commentService;

	@Autowired
	private AirlineService	airlineService;


	// Constructors -----------------------------------------------------------

	public CommentUserController() {
		super();
	}

	// Create ----------------------------------------------------------------		

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create(@RequestParam final int airlineId) {

		ModelAndView result;
		Comment comment;
		Airline airline;

		airline = this.airlineService.findOne(airlineId);

		comment = this.commentService.create(airline);
		result = this.createEditModelAndView(comment);

		return result;
	}

	//Edit--------------------------------------------------------------

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "edit")
	public ModelAndView edit(@RequestParam final int commentId) {
		ModelAndView result;
		Comment comment;

		comment = this.commentService.findOne(commentId);
		Assert.notNull(comment);
		result = this.createEditModelAndView(comment);

		return result;
	}

	//Save---------------------------------------------------------------

	@RequestMapping(value = "/create", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@Valid Comment comment, final BindingResult binding) {

		ModelAndView result;

		if (binding.hasErrors()) {
			System.out.println(binding.toString());
			result = this.createEditModelAndView(comment);

		} else
			try {
				comment = this.commentService.save(comment);
				result = new ModelAndView("redirect:../../airline/display.do?airlineId=" + comment.getAirline().getId());

			} catch (final Throwable oops) {
				System.out.println(oops);

				result = this.createEditModelAndView(comment, "comment.commit.error");

			}
		return result;
	}

	//Ancillary Methods-----------------------------------------------------

	protected ModelAndView createEditModelAndView(final Comment comment) {
		ModelAndView result;

		result = this.createEditModelAndView(comment, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final Comment comment, final String message) {
		ModelAndView result;

		result = new ModelAndView("comment/create");
		result.addObject("comment", comment);
		result.addObject("message", message);

		return result;
	}
}
