package net.bandoviet.place;

import net.bandoviet.tool.FileService;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;





/**
 * Controller (MVC) for places.
 * 
 * @author quocanh
 *
 */
@Controller
public class PlaceController {
  private static final Logger LOGGER = LoggerFactory.getLogger(PlaceController.class);
  
  private static final String PLACE_PATH = "/place/";
  private static final String PLACES_PATH = "/places/";
  private static final String PLACES_CATEGORY_PATH = "/places/category/";
  private static final String PLACES_KEYWORDS_PATH = "/places/searchterms/";

  private final PlaceService placeService;

  @Autowired
  public PlaceController(final PlaceService placeService) {
    this.placeService = placeService;
  }

  /**
   * index.
   * 
   * @param model
   *          communication between frontend and backend
   * @param request
   *          get user's ip
   * @return list of filtered POIs
   */
  @RequestMapping(value = "/", method = RequestMethod.GET)
  public String index(Map<String, Object> model, HttpServletRequest request) {
    Integer pageNumber = 1;

    List<Place> items = placeService.searchByKeywords(pageNumber, null);
    
    int current = pageNumber;
    int begin = Math.max(1, current - 5);
    int totalPages = placeService.getTotalPagesByKeywords(null);
    int end = Math.min(begin + 10, totalPages);

    model.put("totalPages", totalPages);
    model.put("beginIndex", begin);
    model.put("endIndex", end);
    model.put("currentIndex", current);
    model.put("items", items);
    model.put("path", PLACES_PATH);
    
    return "index";
  }
  
  /**
   * search by keywords.
   */
  @RequestMapping(value = PLACES_KEYWORDS_PATH 
                          + "{searchTerms}/{lat}/{lng}/{country}/{address}/{pageNumber}", 
      method = RequestMethod.GET)
  public String searchByKeyWordsPagination(Map<String, Object> model, 
      @PathVariable String searchTerms, 
      @PathVariable Double lat, 
      @PathVariable Double lng,
      @PathVariable String country, 
      @PathVariable String address,
      @PathVariable Integer pageNumber) {
    
    System.out.println(lat);
    System.out.println(lng);
    System.out.println(country);
    
    List<Place> items = 
        placeService.searchByKeywordsLocation(pageNumber, searchTerms, lat, lng, country);
    
    int current = pageNumber;
    int begin = Math.max(1, current - 5);
    int totalPages = placeService.getTotalPagesByKeywords(searchTerms);
    int end = Math.min(begin + 10, totalPages);

    model.put("totalPages", totalPages);
    model.put("beginIndex", begin);
    model.put("endIndex", end);
    model.put("currentIndex", current);
    model.put("items", items);
    model.put("path", PLACES_KEYWORDS_PATH
                      + searchTerms + "/" + lat + "/" + lng + "/" + country + "/" + address + "/");
    
    model.put("keywords", searchTerms);
    model.put("lat", lat);
    model.put("lng", lng);
    model.put("country", country);
    model.put("address", address);
    
    return "index";
  }
  
  /**
   * @return POIs by pagination.
   */
  @RequestMapping(value = PLACES_PATH + "{pageNumber}", method = RequestMethod.GET)
  public String indexPagination(Map<String, Object> model, 
      @PathVariable Integer pageNumber, HttpServletRequest request) {

    List<Place> items = placeService.searchByKeywords(pageNumber, null);
    
    int current = pageNumber;
    int begin = Math.max(1, current - 5);
    int totalPages = placeService.getTotalPagesByKeywords(null);
    int end = Math.min(begin + 10, totalPages);

    model.put("totalPages", totalPages);
    model.put("beginIndex", begin);
    model.put("endIndex", end);
    model.put("currentIndex", current);
    model.put("items", items);
    model.put("path", PLACES_PATH);
    
    return "index";
  }
  
  /**
   * search by category with pagination.
   */
  @RequestMapping(value = PLACES_CATEGORY_PATH + "{type}/{pageNumber}", method = RequestMethod.GET)
  public String searchByCategoryPagination(Map<String, Object> model, 
      @PathVariable String type, @PathVariable Integer pageNumber, HttpServletRequest request) {

    List<Place> items = placeService.searchByCategory(pageNumber, type);
    int current = pageNumber;
    int begin = Math.max(1, current - 5);
    int totalPages = placeService.getTotalPagesByCategory(type);
    int end = Math.min(begin + 10, totalPages);

    model.put("totalPages", totalPages);
    model.put("beginIndex", begin);
    model.put("endIndex", end);
    model.put("currentIndex", current);
    model.put("items", items);
    model.put("path", PLACES_CATEGORY_PATH + type + "/");
    
    model.put("keywords", type);
    
    return "index";
  }
  
  
  /**
   * Open each POI in its propre page.
   */
  @RequestMapping(value = PLACE_PATH + "{titleWithoutAccents}/{id}", method = RequestMethod.GET)
  public String showPlace(Map<String, Object> model, @PathVariable String titleWithoutAccents,
      @PathVariable Long id) {
    Place place = placeService.getPlace(id);
    model.put("place", place);
    return "portfolio";
  }
  /**
   * Show search results for the given query.
   *
   * @param keywords
   *          The search query.
   */
  @RequestMapping(value = { "/search/", "/search" }, method = RequestMethod.GET)
  public String search(@RequestParam String keywords, 
      Map<String, Object> model, HttpServletRequest request) {
    List<Place> items = placeService.search(keywords);
    //System.out.println(items.size());
    model.put("items", items);
    model.put("keywords", keywords);
    return "index";
  }
  
  /**
   * Search by category. '/category/INDIVIDUAL/0';
   * @param type given category.
   * @param model MVC communication.
   * @return list of places.
   */
  @RequestMapping(value = {"/category", "/category/"}, method = RequestMethod.GET)
  public String searchByCategory(
      @RequestParam(value = "type", required = false, defaultValue = "INDIVIDUAL") String type, 
      Map<String, Object> model) {
    List<Place> items = placeService.searchByCategory(type);
    //System.out.println(items.size());
    model.put("items", items);
    model.put("keywords", "");
    model.put("type", PlaceType.valueOf(type));
    return "index";
  }
  
  /**
   * Update the item.
   * @param id item's id
   * @param lang language
   * @return fomule to update
   */
  @RequestMapping(value = {"/update"}, method = RequestMethod.GET)
  public String update(@RequestParam Long id, 
      @RequestParam(value = "vn", required = false) String lang, 
      Map<String, Object> model) {
    LOGGER.info("Received request to update the place whose id=" + id);
    Place place = placeService.getPlace(id);
    if (!StringUtils.isBlank(lang)) {
      model.put("lang", lang);
    }
    model.put("place", place);    
    model.put("typeList", PlaceType.values());
    return "edit";
  }
  
  /**
   * Request to create a new place.
   * @param lang language
   * @param model parameters communication
   * @return form to be filled out
   */
  @RequestMapping(value = {"/create"}, method = RequestMethod.GET)
  public String create(@RequestParam(value = "vn", required = false) String lang,
      Map<String, Object> model) {
    LOGGER.info("Received request to create a new place");
    Place place = placeService.initNewPlace();
    initModel(place, model, lang);
    return "edit";
  }
  
  private void initModel(Place place, Map<String, Object> model, String lang) {
    if (!StringUtils.isBlank(lang)) {
      model.put("lang", lang);
    }
    model.put("place", place);
    model.put("typeList", PlaceType.values());    
  }
  /**
   * Save edited place.
   * @param model params commmunication
   * @param place to be saved
   * @param image image to be saved
   * @param request get user's info
   * @return the saved place
   */
  @RequestMapping(value = {"/save"}, method = RequestMethod.POST)
  public ModelAndView save(Map<String, Object> model, 
      @ModelAttribute("place") @Valid final Place place,       
      @RequestParam("image") MultipartFile image,
      BindingResult result,
      HttpServletRequest request ) {
    LOGGER.info("Received request to save {}, with result={}", place, result);
    if (result.hasErrors()) {
      initModel(place, model, "vn");
      return new ModelAndView("edit");
    }
 
    place.setCreatedFromIp(request.getRemoteAddr());
    Place updatedPlace = placeService.save(place);
    
    try {      
      String imagePath = FileService.saveFile(image, updatedPlace.getId(), "place");
      if (StringUtils.isBlank(imagePath)) {
        if (StringUtils.isNotBlank(place.getImagePath()) 
            && place.getImagePath().indexOf("http") >= 0) {
          imagePath = FileService.saveImage(place.getImagePath(),
              updatedPlace.getId(), "place"); 
        } else if (StringUtils.isBlank(place.getImagePath())) {
          imagePath = FileService.saveImageFromGoogleStreetView(
              updatedPlace.getLatitude(), place.getLongitude(),  
              updatedPlace.getId(), "place");        
        }
      }
      if (StringUtils.isNotBlank(imagePath)) {
        updatedPlace.setImagePath(imagePath);
        updatedPlace.setIconPath(imagePath.substring(0, imagePath.lastIndexOf("/") + 1)
            .concat("icon.jpg"));
        placeService.save(updatedPlace);       
      }

    } catch (Exception e) {
      LOGGER.error("Tried to save user with id", e);
      //result.reject("home.save.error");
      //return "edit";
    }

    // RETURN THE ADDED PLACE
    return new ModelAndView("redirect:" + PLACE_PATH 
        + place.getTitleWithoutAccents() + "/" + place.getId());
  }
    
  /**
   * Suggestions when typing search keywords.
   * @param query keywords.
   * @return list of suggestions.
   */
  @RequestMapping(value = {"/autocomplete"}, method = RequestMethod.GET)
  @ResponseBody public  List<String> autocomplete(
      @RequestParam String query,
      @RequestParam String lat,
      @RequestParam String lng,
      @RequestParam String country) {
    //LOGGER.info(query);
    List<String> results = new ArrayList<String>();
    List<Place> items = placeService.search(query);
    for (Place place : items) {
      results.add(place.getTitle() + ": " + place.getAddress());
    }
    return results;
  }

} // class PlaceController
