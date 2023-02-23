(ns bootstrap)


(def bootstrap-header "<!doctype html>
<html lang='en'>
  <head>
    <!-- Required meta tags -->
    <meta charset='utf-8'>
    <meta name='viewport' content='width=device-width, initial-scale=1'>
    <!-- Bootstrap CSS -->
    <link href='https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css' rel='stylesheet' integrity='sha384-EVSTQN3/azprG1Anm3QDgpJLIm9Nao0Yz1ztcQTwFspd3yD65VohhpuuCOmLASjC' crossorigin='anonymous'>
  </head>
  <body>
    <script src='https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js' integrity='sha384-MrcW6ZMFYlzcLA8Nl+NtUVF0sA7MsXsP1UyJoMp4YLEuNSfAP+JcXn/tWtIaxVXM' crossorigin='anonymous'></script>

<nav class='navbar navbar-expand-lg navbar-light' style='background-color: #e3f2fd;'>
  <div class='container-fluid'>
    <a class='navbar-brand' href='#'>Table Explorer</a>
    <button class='navbar-toggler' type='button' data-bs-toggle='collapse' data-bs-target='#navbarSupportedContent' aria-controls='navbarSupportedContent' aria-expanded='false' aria-label='Toggle navigation'>
      <span class='navbar-toggler-icon'></span>
    </button>
<!--
    <div class='collapse navbar-collapse' id='navbarSupportedContent'>
      <ul class='navbar-nav me-auto mb-2 mb-lg-0'>
        <li class='nav-item'>
          <a class='nav-link active' aria-current='page' href='#'>Home</a>
        </li>
      </ul>
      <form class='d-flex'>
      <ul class='navbar-nav me-auto mb-2 mb-lg-0'>
        <li class='nav-item'>
          <a class='nav-link' aria-current='page' href='#'>About</a>
        </li>
</ul>
      </form>
    </div>
-->
  </div>
</nav>

  <div style='margin: 20px' class='content'>
")

(defn wrap [ content ]
    {  :status 200
     :headers {"Content-Type" "text/html"}
     :body  (str bootstrap-header content "</div></body></html>")})
