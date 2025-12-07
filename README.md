# Website fullstack Fahasa

## Cài đặt môi trường

**1. Clone repository**

```
git clone https://github.com/lamquang4/fullstack-vitejs-books.git
```

**2. Chạy website bằng Docker**

```
docker compose up --build
```

## Mục lục

[1. Giới thiệu phần mềm](#1-giới-thiệu-phần-mềm)

&nbsp;&nbsp;[1.1. Tổng quan dự án](#11-tổng-quan-dự-án)

&nbsp;&nbsp;[1.2. Công nghệ sử dụng](#12-công-nghệ-sử-dụng)

&nbsp;&nbsp;[1.3. Thiết kế phần mềm](#13-thiết-kế-phần-mềm)

&nbsp;&nbsp;&nbsp;[1.3.1. Bối cảnh kinh doanh](#131-bối-cảnh-kinh-doanh)

&nbsp;&nbsp;&nbsp;[1.3.2. Quy trình nghiệp vụ](#132-quy-trình-nghiệp-vụ)

&nbsp;&nbsp;&nbsp;[1.3.3. Use Case](#133-use-case)

&nbsp;&nbsp;&nbsp;[1.3.4. Domain Driven Design](#134-domain-driven-design)

&nbsp;&nbsp;&nbsp;[1.3.5. Data Model](#135-data-model)

&nbsp;&nbsp;[1.4. Thiết kế kiến trúc](#3-thiết-kế-kiến-trúc)

&nbsp;&nbsp;&nbsp;[1.4.1. Sơ đồ khối](#141-sơ-đồ-khối)

&nbsp;&nbsp;&nbsp;[1.4.2. Kiến trúc C4](#142-kiến-trúc-c4)

&nbsp;&nbsp;&nbsp;[1.4.3. Sơ đồ triển khai](#143-sơ-đồ-triển-khai)

[2. Kế hoạch kiểm thử](#2-kế-hoạch-kiểm-thử)

&nbsp;&nbsp;[2.1. Danh mục kiểm thử](#21-danh-mục-kiểm-thử)

&nbsp;&nbsp;[2.2. Chiến lược kiểm thử](#22-chiến-lược-kiểm-thử)

[3. Thiết kế kiểm thử](#3-thiết-kế-kiểm-thử)

## 1. Giới thiệu phần mềm

### 1.1. Tổng quan dự án

Dự án “Website thương mại điện tử Fahasa” là một hệ thống bán sách giấy trực tuyến được xây dựng nhằm mô phỏng hoạt động của một nền tảng thương mại điện tử thực tế.

Để đảm bảo chất lượng phần mềm, quá trình kiểm thử website phải được thực hiện. Kiểm thử giúp đánh giá tính đúng đắn của dữ liệu, mức độ ổn định của hệ thống, khả năng xử lý yêu cầu, tính bảo mật và hiệu năng khi hoạt động trong môi trường thực. Các hoạt động kiểm thử bao gồm xây dựng và thực hiện các test case, kiểm thử đơn vị, kiểm thử tích hợp, kiểm thử hệ thống và kiểm thử chấp nhận trước khi triển khai thực tế.

### 1.2. Công nghệ sử dụng

| Danh mục       | Tools / Frameworks                                           |
| -------------- | ------------------------------------------------------------ |
| Frontend       | Vite, ReactJS, TypeScript                                    |
| Backend        | Spring Boot, Maven                                           |
| Database       | MySQL                                                        |
| Authentication | Spring Security, JWT                                         |
| Testing        | JUnit, Mockito, Spring Boot Test, MockMvc, H2                |
| CI/CD          | GitHub Actions                                               |
| Deployment     | Render deploy Frotnend và Backend, Aiven host Database MySQL |

## 1.3. Thiết kế phần mềm

### 1.3.1. Bối cảnh kinh doanh

Với quản lý sản phẩm, khách hàng có thể tìm kiếm và sắp xếp các đầu sách theo các tiêu chí tên sách, tác giả, nhà xuất bản và danh mục. Khi nhấp vào một cuốn sách bất kỳ, khách hàng sẽ được chuyển đến trang chi tiết sản phẩm, nơi hiển thị đầy đủ thông tin gồm tên sách, tác giả, nhà xuất bản, mô tả nội dung, thông tin chi tiết, cùng hình ảnh minh họa của sách.

Quản trị viên hệ thống hoặc nhân viên bán hàng có thể thêm, sửa, xóa sách cũng như quản lý tác giả, danh mục và nhà xuất bản.

Với quản lý giỏ hàng, khách hàng cần phải đăng nhập mới có thể thêm bất kỳ cuốn sách nào vào giỏ hàng trong trang chi tiết sách. Sau khi thêm, khách hàng có thể xem toàn bộ danh sách sách trong giỏ, bao gồm tên sách, giá, giảm giá và số lượng. Hệ thống cho phép khách hàng thay đổi số lượng hoặc xóa sách khỏi giỏ hàng, đồng thời tự động cập nhật tổng tiền sau mỗi thao tác.

Với quản lý đơn hàng, khách hàng chọn đặt hàng, hệ thống sẽ tự động tạo đơn hàng, ghi nhận thông tin sản phẩm, thông tin giao hàng, phương thức thanh toán.

Quản trị viên hệ thống và nhân viên bán hàng có thể theo dõi và cập nhật trang thái đơn hàng. Nếu đơn hàng có phương thức thanh toán là Momo bị hủy hoặc trả hàng, hệ thống sẽ tự động hoàn tiền cho khách hàng.

Với quản lý thanh toán, hệ thống hỗ thanh toán khi nhận hàng và thanh toán trực tuyến qua cổng Momo. Sau khi thanh toán thành công, hệ thống ghi nhận giao dịch, cập nhật trạng thái đơn hàng, tự động trừ số lượng sách hiện có tương ứng với số lượng đặt mua và chuyển khách hàng đến trang thông báo đặt hàng thành công. Trong trường hợp thanh toán thành công nhưng số lượng sách không đủ để đáp ứng đơn hàng, hệ thống sẽ tự động hoàn tiền và chuyển khách hàng đến trang thông báo đặt hàng thất bại.
Quản trị viên hệ thống và nhân viên bán hàng có thể theo dõi, tra cứu các giao dịch thanh toán thành công hoặc hoàn tiền.

Với quản lý sổ địa chỉ, khách hàng có thể lưu trữ nhiều địa chỉ giao hàng hoặc có thể nhập thông tin địa chỉ trực tiếp trong quá trình thanh toán. Hệ thống cho phép người dùng thêm, sửa hoặc xóa địa chỉ để thuận tiện cho các lần đặt hàng.

Với kiểm soát truy cập, hệ thống cung cấp chức năng đăng nhập, đăng xuất và quản lý người dùng. Quản trị viên hệ thống có thể thêm, sửa, xóa hoặc khóa tài khoản các người dùng. Đảm bảo phân quyền rõ ràng, chỉ cho phép người dùng thực hiện các hành động phù hợp với chức vụ của họ.

### 1.3.2. Quy trình nghiệp vụ

**Quy trình xử lý giỏ hàng**

![](docs/images/qtnv1.png)

**Quy trình đặt hàng và thanh toán**

![](docs/images/qtnv2.png)

**Quy trình quản trị hệ thống**

![](docs/images/qtnv3.png)

### 1.3.3. Use case

**Use case summary**

![](docs/images/ucs.png)

**UC1 Quản lý sản phẩm**

![](docs/images/uc1.png)

**UC2 Quản lý giỏ hàng**

![](docs/images/uc2.png)

**UC3 Quản lý đơn hàng**

![](docs/images/uc3.png)

**UC4 Quản lý thanh toán**

![](docs/images/uc4.png)

**UC5 Quản lý sổ địa chỉ**

![](docs/images/uc5.png)

**UC6 Kiểm soát truy cập**

![](docs/images/uc6.png)

### 1.3.4. Domain driven design

**Mô tả miền nghiệp vụ**

Những lý do chính để chọn miền nghiệp vụ này:

1. Đã có hệ thống thực tế cho miền nghiệp vụ này, vì vậy mọi người có thể đối chiếu phần triển khai với một trang web đang hoạt động.

2. Miền nghiệp vụ cũng không quá đơn giản, vẫn có một số nghiệp vụ phức tạp và logic, không đơn thuần là các thao tác CRUD.

3. Miền nghiệp vụ không quá phức tạp nên dễ hiểu và không quá lớn và dễ triển khai.

Sản phẩm có các thực thể chính gồm Sách, Tác giả, Danh mục và Nhà xuất bản. Khách hàng có thể xem, tìm kiếm và lọc danh sách Sách. Quản trị viên hệ thống và nhân viên bán hàng có thể CRUD đối với Sách, Danh mục, Tác giả và Nhà xuất bản.

Giỏ hàng chỉ khách hàng đã đăng nhập mới có thể thêm Sách vào Giỏ hàng. Khách hàng có thể xem lại, thay đổi số lượng hoặc xóa sách khỏi Giỏ hàng và sau đó tiến hành đặt hàng và thanh toán.

Đơn hàng có thực thể chính là Đơn hàng và Chi tiết đơn hàng. Sau khi đã thêm sản phẩm vào giỏ hàng, khách hàng nhập thông tin giao hàng và phương thức thanh toán sẽ tạo đơn hàng. Quản trị viên hệ thống và nhân viên bán hàng có thể cập nhật trạng thái Đơn hàng.

Thanh toán có thực thể chính là Thanh toán, Phương thức thanh toán, Tình trạng thanh toán. Khách hàng có thể lựa chọn thanh toán bằng Momo. Quản trị viên hệ thống và nhân viên bán hàng có thể xem danh sách các giao dịch Thanh toán.

Sổ địa chỉ có thưc thể chính là Địa chỉ. Khách hàng có thể thêm, sửa hoặc xóa Địa chỉ.

Kiểm soát truy cập có thực thể chính là Người dùng và Chức vụ. Người dùng gồm Khách hàng, Quản trị viên hệ thống và Nhân viên bán hàng. Mỗi người dùng có thể đăng nhập vào hệ thống, đăng xuất khỏi hệ thống. Quản trị viên hệ thống có quyền quản lý các tài khoản người dùng.

**Mô hình khái niệm**

![](docs/images/conceptualmodel.png)

### 1.3.5. Data model

**Mô hình thực thể kết hợp mức khái niệm**

![](docs/images/erd1.png)

**Mô hình thực thể kết hợp mức logic**

![](docs/images/erd2.png)

**Mô hình thực thể kết hợp mức vật lý**

![](docs/images/erd3.png)

## 1.4. Thiết kế kiến trúc

### 1.4.1. Sơ đồ khối

![](docs/images/block.png)

### 1.4.2. Kiến trúc C4

**C1 - System context**

![](docs/images/c1.png)

**C2 – Container**

![](docs/images/c2.png)

**C3 – Component (high-level)**

![](docs/images/c3.png)

**C4 – Code**

**Sơ đồ lớp**

![](docs/images/class.png)

**Sơ đồ sequence**

**Quản lý sản phẩm**

![](docs/images/seq1.png)

**Quản lý giỏ hàng**

![](docs/images/seq2.png)

**Quản lý thanh toán**

![](docs/images/seq3.png)

**Quản lý đơn hàng**

![](docs/images/seq4.png)

**Quản lý sổ địa chỉ**

![](docs/images/seq5.png)

**Kiểm soát truy cập**

![](docs/images/seq6.png)

### 1.4.3. Sơ đồ triển khai

![](docs/images/deployment.png)

## 2. Giới thiệu phần mềm

### 2.1. Danh mục kiểm thử

**Các tính năng được kiểm thử**

Tất cả các yêu cầu chức năng nghiệp vụ của hệ thống trong tài liệu sẽ được kiểm thử, bao gồm:

Kiểm soát truy cập: Đăng ký, đăng nhập (cho 3 chức vụ), đăng xuất (cả 2 phía quản trị viên và khách hàng). Quản lý người dùng (phía quản trị viên)

Quản lý sản phẩm: Chức năng quản lý sách, danh mục, tác giả, nhà xuất bản (phía quản trị viên). Chức năng tìm kiếm, lọc và sắp xếp sách (phía khách hàng).

Quản lý giỏ hàng: Thêm, cập nhật số lượng, xóa sách khỏi giỏ hàng (phía khách hàng).

Quản lý đơn hàng: Quy trình đặt hàng, xem lịch sử đơn hàng (phía khách hàng). Cập nhật trạng thái đơn hàng (phía quản trị viên).

Quản lý thanh toán: Quy trình xử lý thanh toán bằng cổng Momo thành công và thất bại (phía khách hàng). Xem các giao dịch thanh toán Momo (phía quản trị viên).

Quản lý sổ địa chỉ: Thêm, cập nhật và xóa địa chỉ (phía khách hàng).

**Các tính năng không được kiểm thử**

Các phi chức năng sau sẽ không được kiểm thử do giới hạn về thời gian và nguồn lực:

Kiểm thử hiệu năng (Performance Testing) gồm kiểm thử tải (Load Test) và kiểm thử căng thẳng (Stress Test) sẽ không được thực hiện.

Kiểm thử tính khả dụng (Usability Testing) sẽ không có đánh giá chính thức về đánh giá trải nghiệm người dùng (UX) và độ thân thiện của giao diện (UI), chỉ được thực hiện ở mức cơ bản trong quá trình kiểm thử hệ thống.

Kiểm thử tương thích (Compatibility Testing) sẽ không kiểm thử công trên các trình duyệt khác, chỉ tập trung vào trình duyệt Google Chrome trên máy tính và điện thoại.

Hệ thống của bên thứ ba sẽ không được kiểm thử nội bộ hệ thống của Momo; chỉ kiểm thử API tích hợp (đầu vào/đầu ra) giữa Backend và cổng thanh toán Momo.

### 2.2. Chiến lược kiểm thử

Dự án áp dụng phương pháp kiểm thử theo khung nhìn V-Model, trong đó kiểm thử được tiến hành song song với từng giai đoạn phát triển phần mềm.

Tích hợp CI/CD bằng việc sử dụng GitHub Actions để tự động hóa quy trình Build, Test và Deploy.

| Loại kiểm thử      | Mục đích                                       | Kỹ thuật  | Phương pháp                                           |
| ------------------ | ---------------------------------------------- | --------- | ----------------------------------------------------- |
| Kiểm thử đơn vị    | Kiểm tra tính đúng đắn của các hàm/phương thức | Hộp trắng | Kiểm thử tự động bằng JUnit và Mockito                |
| Kiểm thử tích hợp  | Kiểm tra sự tương tác giữa các module          | Hộp trắng | Kiểm thử tự động bằng Spring Boot Test, MockMvc và H2 |
| Kiểm thử hệ thống  | Kiểm tra các luồng nghiệp vụ và giao diện      | Hộp đen   | Kiểm thử thủ công cho các Test Case                   |
| Kiểm thử chấp nhận | Kiểm tra và nghiệm thu sản phẩm                | Hộp đen   | Kiểm thử thủ công                                     |
