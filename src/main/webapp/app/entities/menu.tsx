import React from 'react';

import MenuItem from 'app/shared/layout/menus/menu-item';

const EntitiesMenu = () => {
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/address">
        Address
      </MenuItem>
      <MenuItem icon="asterisk" to="/chief">
        Chief
      </MenuItem>
      <MenuItem icon="asterisk" to="/chief-links">
        Chief Links
      </MenuItem>
      <MenuItem icon="asterisk" to="/city">
        City
      </MenuItem>
      <MenuItem icon="asterisk" to="/dish">
        Dish
      </MenuItem>
      <MenuItem icon="asterisk" to="/kitchen">
        Kitchen
      </MenuItem>
      <MenuItem icon="asterisk" to="/menu">
        Menu
      </MenuItem>
      <MenuItem icon="asterisk" to="/order-item">
        Order Item
      </MenuItem>
      <MenuItem icon="asterisk" to="/orders">
        Orders
      </MenuItem>
      <MenuItem icon="asterisk" to="/signature-dish">
        Signature Dish
      </MenuItem>
      <MenuItem icon="asterisk" to="/users">
        Users
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
