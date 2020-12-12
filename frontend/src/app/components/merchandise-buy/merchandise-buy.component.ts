import { Component, OnInit } from '@angular/core';
import {Merchandise} from '../../dtos/merchandise';
import {MerchandiseService} from '../../services/merchandise.service';
import {ActivatedRoute} from '@angular/router';
import {DomSanitizer} from '@angular/platform-browser';
import {PhotosService} from '../../services/photos.service';
import {UserService} from '../../services/user.service';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';

@Component({
  selector: 'app-merchandise-buy',
  templateUrl: './merchandise-buy.component.html',
  styleUrls: ['./merchandise-buy.component.css']
})
export class MerchandiseBuyComponent implements OnInit {

  merchandise: Merchandise;
  merchandiseId: number;
  photo: string;

  quantity: number = 1;
  wrongInput: boolean = false;

  totalPrice: number = 0;
  paymentType: string = 'credit';
  months = ['01', '02', '03', '04', '05', '06', '07', '08', '09', '10', '11', '12'];
  years: number[] = [];
  banks: string[] = [
    'Alpenbank Aktiengesellschaft',
    'Bank Austria Creditanstalt AG',
    'Bank Burgenland',
    'BAWAG',
    'DenizBank AG',
    'Hypo Bank Niederösterreich',
    'Raiffeisenbank',
    'Zveza Bank'];
  paying: boolean = false;
  paymentSuccessful: boolean = false;

  cardForm: FormGroup;
  paypalForm: FormGroup;
  immediateForm: FormGroup;

  submitted: boolean = false;


  constructor(private merchandiseService: MerchandiseService,
              private userService: UserService,
              private route: ActivatedRoute,
              private sanitizer: DomSanitizer,
              private photosService: PhotosService,
              private formBuilder: FormBuilder) {}

  ngOnInit(): void {
    this.merchandiseId = Number(this.route.snapshot.paramMap.get('id'));
    this.merchandiseService.getMerchandiseItemById(this.merchandiseId).subscribe(
      (merchandise: Merchandise) => {
        this.merchandise = merchandise;
        this.getImage();
        /*this.getRandomImage();*/
        this.totalPrice = this.merchandise.price;
      }
    );
    const year = new Date().getFullYear();
    for (let i = year; i < year + 50; i++) {
      this.years.push(i);
    }
    this.setUpForm();
  }

  setUpForm() {
    this.cardForm = this.formBuilder.group({
      owner: new FormControl('', Validators.compose([
        Validators.required,
      ])),
      cardNumber: new FormControl('', Validators.compose([
        Validators.required,
        Validators.pattern('^[0-9]{12,16}$')
      ])),
      month: new FormControl('01', Validators.required),
      year: new FormControl(new Date().getFullYear(), Validators.required),
      cvv: new FormControl('', Validators.compose([
        Validators.required,
        Validators.pattern('^[0-9]{3}$')
      ]))
    }, {validator: this.validateCreditCard('cardNumber')});
    this.paypalForm = new FormGroup({
      emailAddress: new FormControl(localStorage.getItem('loggedInUserEmail'), [Validators.required, Validators.pattern('([\\w-\\.]+)@((?:[\\w]+\\.)+)([a-zA-Z]{2,4})')]),
      password: new FormControl('', Validators.required)
    });
    this.immediateForm = new FormGroup({
      bank: new FormControl(this.banks[0], Validators.required),
      signatoryNumber: new FormControl('', [Validators.required, Validators.pattern('^[A-Z0-9]{6,8}$')]),
      pin: new FormControl('', [Validators.required, Validators.pattern('^[0-9]{4,6}$')])
    });
  }

  /**
   * Use sanitizer to secure image
   */
  getSanitizer() {
    return this.sanitizer.bypassSecurityTrustResourceUrl(this.merchandise.image);
  }

  getImage() {
    this.merchandiseService.getImages(this.merchandiseId).subscribe(
      (res) => {
        this.merchandise.imagePresent = true;
        this.merchandise.image = res;
      });
  }

  getRandomImage() {
    this.photosService.getRandomImage().subscribe(
      res => {
        console.log(res);
        this.photo = res.urls.regular;
      }, error1 => {
        console.log(error1);
      }
    );

  }

  incrementQuantity(): void {
    this.quantity++;
  }

  decrementQuantity(): void {
    this.quantity--;
  }

  purchase() {
    this.submitted = true;
    let validInput = false;
    switch (this.paymentType) {
      case 'credit': {
        validInput = this.cardForm.valid;
        break;
      }
      case 'paypal': {
        validInput = this.paypalForm.valid;
        break;
      }
      case 'immediate': {
        validInput = this.immediateForm.valid;
        break;
      }
    }
    if (validInput) {
      this.paying = true;
      setTimeout(() => {
        this.paymentSuccessful = true;
        this.paying = false;
      }, 3000);
    } else {
      console.log('Invalid input in: ' + this.paymentType);
    }
  }

  private calculateTotalPrice() {
    this.totalPrice = this.merchandise.price * this.quantity;
  }

  changePaymentType() {
    this.submitted = false;
    this.setUpForm();
  }

  /**
   * validates a given card number based on the Luhn Algorithm.
   * @param controlName specifies the control that contains the card number.
   */
  validateCreditCard(controlName) {
    return (formGroup: FormGroup) => {
      const control = formGroup.controls[controlName];
      if (!control.value) {
        control.setErrors({required: true});
      } else if (/[^0-9-\s]+/.test(control.value)) {
        control.setErrors({pattern: true});
      } else {
        let nCheck = 0, nDigit = 0, bEven = false;
        const value = control.value.replace(/\D/g, '');
        for (let n = value.length - 1; n >= 0; n--) {
          const cDigit = value.charAt(n);
          nDigit = parseInt(cDigit, 10);
          if (bEven) {
            if ((nDigit *= 2) > 9) {
              nDigit -= 9;
            }
          }
          nCheck += nDigit;
          bEven = !bEven;
        }

        if ((nCheck % 10) !== 0) {
          control.setErrors({invalid: true});
        }
      }
    };
  }

  /**
   * Checks if the quantity input is allowed (>= 1) and corrects it if necessary.
   * Called every time the quantity input is changed.
   */
  correctQuantity() {
    if (this.quantity < 1) {
      this.quantity = 1;
      this.wrongInput = true;
    } else {
      this.wrongInput = false;
    }
  }
}
