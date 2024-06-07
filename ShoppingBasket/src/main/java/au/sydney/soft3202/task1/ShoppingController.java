package au.sydney.soft3202.task1;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.data.domain.Example;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

class Item extends RepresentationModel<Item> {
    public Integer id;
    public String name;
    public String user;
    public int count;
    public Double cost;

    public Item(int id, String name, String user, int count, Double cost) {
        this.id = id;
        this.name = name;
        this.user = user;
        this.count = count;
        this.cost = cost;
    }

    public Item() {
    }
}


@RestController
@RequestMapping("/api/shop")
public class ShoppingController {

    Map<String, Double> costs = new HashMap<String, Double>();

    @Autowired
    private ShoppingBasketRepository shoppingBasket;

    @GetMapping("viewall")
    public List<Item> getBasket() {
        List<ShoppingBasket> fs = shoppingBasket.findAll();
        List<Item> bs = new LinkedList<Item>();
        for (ShoppingBasket f : fs) {
            if (!costs.containsKey(f.getName())) {
                costs.put(f.getName(), 0.00);
            }
            bs.add(new Item(f.getId(), f.getName(), f.getUser(), f.getCount(), costs.get(f.getName())));
        }
        return bs;
    }

    @GetMapping("users")
    public List<String> getUsers() {
        List<ShoppingBasket> fs = shoppingBasket.findAll();
        Set<String> users = new LinkedHashSet<>();
        for (ShoppingBasket f : fs) {
            users.add(f.getUser());
        }
        return new ArrayList<>(users);
    }

    @GetMapping("costs")
    public Map<String, Double> costs() {
        List<ShoppingBasket> fs = shoppingBasket.findAll();
        Map<String, Double> costMap = new HashMap<>();
        for (ShoppingBasket f : fs) {
            if (!costs.containsKey(f.getName())) {
                costMap.put(f.getName(), 0.00);
            } else {
                double cost = f.getCount() * costs.get(f.getName()) + costMap.getOrDefault(f.getName(), 0D);
                costMap.put(f.getName(), cost);
            }
        }
        return costMap;
    }

    @GetMapping("users/{username}")
    public List<Map<String, Integer>> getUserBasket(@PathVariable("username") String username) {
        List<ShoppingBasket> fs = shoppingBasket.findAll();
        Map<String, Integer> itemCountMap = new HashMap<>();
        for (ShoppingBasket f : fs) {
            if (!f.getUser().equals(username)) {
                continue;
            }
            int count = f.getCount() + itemCountMap.getOrDefault(f.getName(), 0);
            itemCountMap.put(f.getName(), count);
        }
        return List.of(itemCountMap);
    }

    @GetMapping("users/{username}/total")
    public Double getUserTotalCost(@PathVariable("username") String username) {
        List<ShoppingBasket> fs = shoppingBasket.findAll();
        double totalCost = 0;
        for (ShoppingBasket f : fs) {
            if (!f.getUser().equals(username)) {
                continue;
            }
            totalCost += f.getCount() * costs.getOrDefault(f.getName(), 0D);
        }
        return totalCost;
    }

    @PostMapping("costs")
    public void addPapaya(@RequestBody Item item) {
        costs.put(item.name, item.cost);
    }

    @PutMapping("/costs/papaya")
    public boolean updatePapaya(@RequestBody Item item) {
        if (costs.containsKey(item.name)) {
            costs.put(item.name, item.cost);
            return true;
        }
        return false;
    }

    @PostMapping(value = "/users/{username}/add")
    public void addBasket(@PathVariable("username") String username, @RequestBody Item item) {
        ShoppingBasket tmp = new ShoppingBasket();
        tmp.setName(item.name);
        tmp.setUser(username);

        Optional<ShoppingBasket> shoppingBasketOptional = shoppingBasket.findOne(Example.of(tmp));
        if (shoppingBasketOptional.isPresent()) {
            tmp = shoppingBasketOptional.get();
            tmp.setCount(tmp.getCount() + item.count);
        } else {
            tmp.setCount(item.count);
        }
        shoppingBasket.saveAndFlush(tmp);
    }

    @PutMapping(value = "/users/{username}/basket/{papaya}")
    public void editBasket(@PathVariable("username") String username, @PathVariable("papaya") String papaya, @RequestBody Item item) {
        ShoppingBasket tmp = new ShoppingBasket();
        tmp.setName(papaya);
        tmp.setUser(username);

        Optional<ShoppingBasket> shoppingBasketOptional = shoppingBasket.findOne(Example.of(tmp));
        if (shoppingBasketOptional.isPresent()) {
            tmp = shoppingBasketOptional.get();
            tmp.setCount(item.count);
            shoppingBasket.saveAndFlush(tmp);
        }
    }

    @DeleteMapping(value = "/users/{username}")
    public void deleteBasket(@PathVariable("username") String username) {
        ShoppingBasket tmp = new ShoppingBasket();
        tmp.setUser(username);
        List<ShoppingBasket> shoppingBasketList = shoppingBasket.findAll(Example.of(tmp));
        if (shoppingBasketList.size() > 0) {
            shoppingBasket.deleteAll(shoppingBasketList);
        }
    }

    @DeleteMapping(value = "/users/{username}/basket/{papaya}")
    public void deleteItem(@PathVariable("username") String username, @PathVariable("papaya") String papaya) {
        ShoppingBasket tmp = new ShoppingBasket();
        tmp.setName(papaya);
        tmp.setUser(username);

        Optional<ShoppingBasket> shoppingBasketOptional = shoppingBasket.findOne(Example.of(tmp));
        shoppingBasketOptional.ifPresent(basket -> shoppingBasket.deleteById(basket.getId()));
    }

}
