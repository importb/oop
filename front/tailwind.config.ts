import type { Config } from "tailwindcss";

const config: Config = {
  content: [
    "./pages/**/*.{js,ts,jsx,tsx,mdx}",
    "./components/**/*.{js,ts,jsx,tsx,mdx}",
    "./app/**/*.{js,ts,jsx,tsx,mdx}",
  ],
  theme: {
    extend: {
      boxShadow: {
        gold: '0 10px 20px -5px rgba(209, 154, 102, 0.1), 0 6px 6px -5px rgba(209, 154, 102, 0.06)'
      },
      backgroundImage: {
        "gradient-radial": "radial-gradient(var(--tw-gradient-stops))",
        "gradient-conic":
          "conic-gradient(from 180deg at 50% 50%, var(--tw-gradient-stops))",
        "header-red": "#BE2B2B 0%",
        "header-purple": "#8F3B9D 100%"
      },
    },
  },
  plugins: [],
};

export default config;
