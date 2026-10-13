// ===== 主题切换 =====
(function () {
  const root = document.documentElement;
  const toggle = document.getElementById("themeToggle");
  const icon = toggle.querySelector(".theme-icon");
  const STORE = "blog-theme";

  function apply(theme) {
    root.setAttribute("data-theme", theme);
    icon.textContent = theme === "dark" ? "☀️" : "🌙";
  }

  // 初始：优先本地存储，否则跟随系统
  const saved = localStorage.getItem(STORE);
  if (saved) {
    apply(saved);
  } else {
    const prefersDark = window.matchMedia("(prefers-color-scheme: dark)").matches;
    apply(prefersDark ? "dark" : "light");
  }

  toggle.addEventListener("click", () => {
    const next = root.getAttribute("data-theme") === "dark" ? "light" : "dark";
    apply(next);
    localStorage.setItem(STORE, next);
  });
})();

// ===== 移动端菜单 =====
(function () {
  const menuBtn = document.getElementById("menuToggle");
  const links = document.getElementById("navLinks");
  menuBtn.addEventListener("click", () => links.classList.toggle("open"));
  links.querySelectorAll("a").forEach((a) =>
    a.addEventListener("click", () => links.classList.remove("open"))
  );
})();

// ===== 滚动揭示动画 =====
(function () {
  const items = document.querySelectorAll(".reveal");
  if (!("IntersectionObserver" in window)) {
    items.forEach((el) => el.classList.add("in"));
    return;
  }
  const io = new IntersectionObserver(
    (entries) => {
      entries.forEach((e) => {
        if (e.isIntersecting) {
          e.target.classList.add("in");
          io.unobserve(e.target);
        }
      });
    },
    { threshold: 0.12 }
  );
  items.forEach((el) => io.observe(el));
})();

// ===== 导航高亮 + 回到顶部 =====
(function () {
  const sections = document.querySelectorAll("section[id]");
  const navLinks = document.querySelectorAll(".nav-links a");
  const backTop = document.getElementById("backTop");

  function onScroll() {
    const y = window.scrollY + 100;
    let current = "";
    sections.forEach((s) => {
      if (y >= s.offsetTop) current = s.id;
    });
    navLinks.forEach((a) => {
      a.classList.toggle("active", a.getAttribute("href") === "#" + current);
    });
    backTop.classList.toggle("show", window.scrollY > 400);
  }

  window.addEventListener("scroll", onScroll, { passive: true });
  onScroll();

  backTop.addEventListener("click", () =>
    window.scrollTo({ top: 0, behavior: "smooth" })
  );
})();
