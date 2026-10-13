// ===== 主题切换 =====
(function () {
  const root = document.documentElement;
  const toggle = document.getElementById("themeToggle");
  if (!toggle) return;
  const icon = toggle.querySelector(".theme-icon");
  const STORE = "blog-theme";

  function apply(theme) {
    root.setAttribute("data-theme", theme);
    icon.textContent = theme === "dark" ? "☀️" : "🌙";
  }

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
  if (!menuBtn || !links) return;
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

// ===== 导航高亮（仅首页 hash 导航） + 回到顶部 =====
(function () {
  const backTop = document.getElementById("backTop");
  const navLinks = document.querySelectorAll(".nav-links a");
  const useHash = navLinks.length && navLinks[0].getAttribute("href").startsWith("#");

  function onScroll() {
    if (backTop) backTop.classList.toggle("show", window.scrollY > 400);
    if (!useHash) return;
    const y = window.scrollY + 100;
    let current = "";
    document.querySelectorAll("section[id]").forEach((s) => {
      if (y >= s.offsetTop) current = s.id;
    });
    navLinks.forEach((a) =>
      a.classList.toggle("active", a.getAttribute("href") === "#" + current)
    );
  }

  window.addEventListener("scroll", onScroll, { passive: true });
  onScroll();
  if (backTop)
    backTop.addEventListener("click", () =>
      window.scrollTo({ top: 0, behavior: "smooth" })
    );
})();

// ===== 文章筛选（分类 / 标签 + URL 参数） =====
(function () {
  const feed = document.getElementById("postFeed");
  const bar = document.getElementById("filterBar");
  const info = document.getElementById("filterInfo");
  if (!feed || !bar) return;

  const cards = Array.from(feed.querySelectorAll(".post-card"));
  const buttons = Array.from(bar.querySelectorAll(".filter-btn"));

  function applyFilter(type, value) {
    let count = 0;
    cards.forEach((card) => {
      let match = true;
      if (type === "cat") match = card.dataset.category === value;
      else if (type === "tag") match = (card.dataset.tags || "").split(" ").includes(value);
      card.style.display = match ? "" : "none";
      if (match) count++;
    });

    if (type === "all") info.textContent = `共 ${count} 篇文章`;
    else if (type === "cat") info.textContent = `分类：${value} · ${count} 篇`;
    else info.textContent = `标签：${value} · ${count} 篇`;

    buttons.forEach((b) =>
      b.classList.toggle(
        "active",
        type === "all"
          ? b.dataset.filterType === "all"
          : b.dataset.filterType === type && b.dataset.filterValue === value
      )
    );
  }

  buttons.forEach((b) =>
    b.addEventListener("click", () =>
      applyFilter(b.dataset.filterType, b.dataset.filterValue)
    )
  );

  // 从 URL 参数预筛选（来自侧边栏的分类/标签链接）
  const params = new URLSearchParams(location.search);
  const cat = params.get("cat");
  const tag = params.get("tag");
  if (cat) applyFilter("cat", cat);
  else if (tag) applyFilter("tag", tag);
  else applyFilter("all", "");
})();
