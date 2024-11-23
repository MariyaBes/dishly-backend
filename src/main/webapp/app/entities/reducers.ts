import address from 'app/entities/address/address.reducer';
import chief from 'app/entities/chief/chief.reducer';
import chiefLinks from 'app/entities/chief-links/chief-links.reducer';
import city from 'app/entities/city/city.reducer';
import dish from 'app/entities/dish/dish.reducer';
import kitchen from 'app/entities/kitchen/kitchen.reducer';
import menu from 'app/entities/menu/menu.reducer';
import orderItem from 'app/entities/order-item/order-item.reducer';
import orders from 'app/entities/orders/orders.reducer';
import signatureDish from 'app/entities/signature-dish/signature-dish.reducer';
import users from 'app/entities/users/users.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  address,
  chief,
  chiefLinks,
  city,
  dish,
  kitchen,
  menu,
  orderItem,
  orders,
  signatureDish,
  users,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
