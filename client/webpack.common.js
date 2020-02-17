const path = require('path');
const webpack = require('webpack');
const { CleanWebpackPlugin } = require('clean-webpack-plugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const VueLoaderPlugin = require('vue-loader/lib/plugin');
const { filter, map, mapValues } = require('lodash');

const CONFIGS = {
  web: {
    entrypoints: {
      index: {
        path: path.resolve('src', 'web', 'pages'),
        html: 'index.html',
        ts: 'Index.ts',
      },
    },
  },
  overwolf: {
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
  },
};

const APIS = {
  local: 'window.location.href',
  external: JSON.stringify('https://iron-quest.herokuapp.com'),
};

const config = CONFIGS[process.env.TARGET] || CONFIGS.WEB;
const api = APIS[process.env.API] || APIS.external;

function entrypoints(options) {
  return map(config.entrypoints, entrypoint => {
    return new HtmlWebpackPlugin({
      filename: entrypoint.html,
      template: path.resolve(entrypoint.path, entrypoint.html),
      excludeChunks: map(
        filter(config.entrypoints, e => e !== entrypoint),
        e => e.html.replace('.html', '')
      ),
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

function tsLoader(configFile) {
  return {
    test: /\.tsx?$/,
    loader: 'ts-loader',
    exclude: /node_modules|vue\/src/,
    options: {
      appendTsSuffixTo: [/\.vue$/],
      configFile: configFile,
    },
  };
}

module.exports = {
  entrypoints,
  styleLoaders,
  tsLoader,
  webpack: {
    entry: mapValues(config.entrypoints, e => path.resolve(e.path, e.ts)),
    module: {
      rules: [
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
      new VueLoaderPlugin(),
      new webpack.DefinePlugin({
        __API__: api,
      }),
    ],
    resolve: {
      alias: {
        vue$: 'vue/dist/vue.esm.js',
        '@': path.resolve(__dirname, 'src'),
      },
      extensions: ['.tsx', '.ts', '.js', '.styl'],
    },
    output: {
      filename: 'js/[name].js',
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
