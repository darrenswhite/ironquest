const path = require('path');
const webpack = require('webpack');
const { CleanWebpackPlugin } = require('clean-webpack-plugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const VueLoaderPlugin = require('vue-loader/lib/plugin');
const { map, mapValues } = require('lodash');

const WEB = {
  target: 'web',
  entrypoints: {
    index: {
      path: path.resolve('src', 'web', 'pages'),
      html: 'index.html',
      ts: 'Index.ts',
    },
  },
};

const OVERWOLF = {
  target: 'overwolf',
  entrypoints: {
    controller: {
      path: path.resolve('src', 'overwolf', 'windows', 'controller'),
      html: 'controller.html',
      ts: 'Controller.ts',
    },
    results: {
      path: path.resolve('src', 'overwolf', 'windows', 'results'),
      html: 'results.html',
      ts: 'Results.ts',
    },
    settings: {
      path: path.resolve('src', 'overwolf', 'windows', 'settings'),
      html: 'settings.html',
      ts: 'Settings.ts',
    },
    username: {
      path: path.resolve('src', 'overwolf', 'windows', 'username'),
      html: 'username.html',
      ts: 'Username.ts',
    },
  },
};

let config;

if (process.env.TARGET === WEB.target) {
  config = WEB;
} else if (process.env.TARGET === OVERWOLF.target) {
  config = OVERWOLF;
} else {
  throw new Error(`Unknown target: ${process.env.TARGET}`);
}

function entrypoints(options) {
  return map(config.entrypoints, entrypoint => {
    return new HtmlWebpackPlugin({
      filename: entrypoint.html,
      template: path.resolve(entrypoint.path, entrypoint.html),
      ...options,
    });
  });
}

function styleLoaders(extract) {
  const styleLoader = extract
    ? MiniCssExtractPlugin.loader
    : 'vue-style-loader';
  const cssLoader = {
    loader: 'css-loader',
    options: {
      sourceMap: true,
    },
  };
  const postCssLoader = {
    loader: 'postcss-loader',
    options: {
      sourceMap: true,
    },
  };

  return [
    {
      test: /\.(s(c|a)ss)$/,
      use: [
        styleLoader,
        cssLoader,
        postCssLoader,
        {
          loader: 'sass-loader',
          options: {
            sourceMap: true,
          },
        },
      ],
    },
    {
      test: /\.styl(us)?$/,
      use: [
        styleLoader,
        cssLoader,
        postCssLoader,
        {
          loader: 'stylus-loader',
          options: {
            sourceMap: true,
          },
        },
      ],
    },
    {
      test: /\.css$/,
      use: [styleLoader, cssLoader, postCssLoader],
    },
  ];
}

module.exports = {
  entrypoints: entrypoints,
  styleLoaders: styleLoaders,
  webpack: {
    entry: mapValues(config.entrypoints, e => path.resolve(e.path, e.ts)),
    module: {
      rules: [
        {
          test: /\.tsx?$/,
          loader: 'ts-loader?configFile=tsconfig.json',
          exclude: /node_modules|vue\/src/,
          options: {
            appendTsSuffixTo: [/\.vue$/],
          },
        },
        {
          test: /\.(png|woff|woff2|eot|ttf|svg)$/,
          use: 'file-loader?limit=1024&name=[path][name].[ext]',
        },
        {
          test: /\.vue$/,
          loader: 'vue-loader',
        },
      ],
    },
    plugins: [
      new CleanWebpackPlugin(),
      new webpack.ProvidePlugin({
        $: 'jquery',
        jQuery: 'jquery',
        'window.jQuery': 'jquery',
      }),
      new VueLoaderPlugin(),
    ],
    resolve: {
      alias: {
        vue$: 'vue/dist/vue.esm.js',
        '@': path.resolve(__dirname, 'src'),
      },
      extensions: ['.tsx', '.ts', '.js', '.styl'],
    },
    output: {
      filename: '[name].js',
      path: path.resolve(__dirname, 'build'),
    },
    node: {
      setImmediate: false,
      dgram: 'empty',
      fs: 'empty',
      net: 'empty',
      tls: 'empty',
      child_process: 'empty',
    },
  },
};
