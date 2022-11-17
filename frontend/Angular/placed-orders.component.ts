import {Component, OnInit} from '@angular/core';
import {OrdersService} from '../../services/orders/orders.service';
import {User} from '../../models/user/user';
import {ProfilesService} from '../../services/profiles/profiles.service';
import {Workbook} from 'exceljs';
import * as fs from 'file-saver';
import {Order} from '../../models/order/order';

@Component({
  selector: 'app-placed-orders',
  templateUrl: './placed-orders.component.html',
  styleUrls: ['./placed-orders.component.css']
})
export class PlacedOrdersComponent implements OnInit {
  private user: User;
  public show: boolean;

  constructor(public orderService: OrdersService, private profileService: ProfilesService) {
  }

  ngOnInit(): void {
    this.profileService.setCurrentUserForView().subscribe(r => {
      this.user = r;
      // set the placed orders for the current user
      this.setPlacedOrdersForUser();

    });

  }

  /**
   * set the placed orders for the current user
   */
  setPlacedOrdersForUser(): any {
    // get all the placed orders
    this.orderService.findRequestPlacedByUser(this.user.id).subscribe((data) => {
      console.log(data);
      // set the data in an array
      this.orderService.placedOrders = data;
    });
  }


  showTable(): any {
    this.show = !this.show;
  }

  /**
   * Make an export of an order
   * @param order the order that is going to be exported
   */
  exportAsXLSX(order: Order): void {
    const title = 'Geplaatste order bevestiging';
    const header = ['OrderNr', 'Aantal', 'Totaalprijs', 'Status', 'Product', 'Verkoper'];
    // tslint:disable-next-line:max-line-length
    const info = [order.id, order.quantity, (order.quantity * order.product.price),
      order.status, order.product.name, order.product.created_by_id['company_name']];

    // create and new Excelsheet
    const workbook = new Workbook();
    // add a new sheet
    const worksheet = workbook.addWorksheet('Order Nr ' + order.id);

    worksheet.addRow([title]);

    worksheet.addRow(header);
    worksheet.addRow(info);

    // export the workbook as a excel file
    workbook.xlsx.writeBuffer().then((data) => {
      const blob = new Blob([data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
      fs.saveAs(blob, 'Order_bevestiging_' + order.id + '.xlsx');
    });
  }

}

