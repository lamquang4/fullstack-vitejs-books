export interface User {
  id?: string;
  fullname: string;
  email: string;
  role?: number;
  password?: string;
  status?: number;
}

export interface Category {
  id?: string;
  name: string;
  slug?: string;
}

export interface Author {
  id?: string;
  fullname: string;
  slug?: string;
}

export interface Publisher {
  id?: string;
  name: string;
  slug?: string;
}

export interface Book {
  id: string;
  images: ImageBook[];
  title: string;
  price: number;
  discount: number;
  description: string;
  publicationDate: string;
  numberOfPages: number;
  weight: number;
  width: number;
  length: number;
  thickness: number;
  slug: string;
  status: number;
  stock: number;
  category: Category;
  author: Author;
  publisher: Publisher;
}

export interface ImageBook {
  id: string;
  image: string;
}

export interface Address {
  id?: string;
  fullname: string;
  phone: string;
  speaddress: string;
  ward: string;
  city: string;
  user?: string;
}

export interface Order {
  id: string;
  orderCode: string;
  userId: string;
  fullname: string;
  phone: string;
  speaddress: string;
  city: string;
  ward: string;
  userFullname: string;
  paymethod: string;
  items: ProductBuy[];
  status: number;
  total: number;
  createdAt: string;
}

export interface ProductBuy {
  id: string;
  bookId: string;
  title: string;
  images: string[];
  quantity: number;
  price: number;
  discount: number;
}

export interface OrderAdd {
  fullname: string;
  phone: string;
  speaddress: string;
  city: string;
  ward: string;
  paymethod: string;
  items: {
    bookId: string;
    quantity: number;
    price: number;
    discount: number;
  }[];
}

export interface ProductInCart {
  id: string;
  bookId: string;
  title: string;
  images: string[];
  name: string;
  price: number;
  discount: number;
  slug: string;
  quantity: number;
  stock: number;
}

export interface Cart {
  id?: string;
  user?: string;
  items: ProductInCart[];
}

export type Ward = {
  name: string;
  mergedFrom: string[];
};

export type Province = {
  id: string;
  province: string;
  wards: Ward[];
};
