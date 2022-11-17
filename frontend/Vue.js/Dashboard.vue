<script>
import axios from "axios";

export default {
  components: "SelectedFilter",
  data() {
    return {
      email: '',
      totalEmails: 0,
      totalThreads: 0,
      filterSet: [],
      filterSetId: 0,
      available_points: 0,
      total_points: 0,
      providerId: this.providerId,
      steps: [],
      loaded: false
    }
  },
  async mounted() {
    this.loaded = false;
    await this.getAccountProfile();
    await this.getFiltersByProvider();
    await this.getAvailablePoints();
    await this.getTotalPoints();
    await this.getSteps();
    this.loaded = true;
  },
  methods: {
    async getSteps() {
      let token = localStorage.getItem('token');
      const config = {
        headers: {Authorization: `Bearer ${token}`}
      };
      await axios.post(import.meta.env.VITE_ROOT_API + "/user/provider/" + this.$route.params.uuid + "/steps", config).then((res) => {
        this.steps = res.data;
      });
    },
    updateFilter() {
      this.$router.push({path: "/filter/" + this.filterSetId})
    },
    async getAccountProfile() {
      await axios.post(import.meta.env.VITE_ROOT_API + "/user/providers/gmail/profile/" + this.$route.params.uuid).then((res) => {
        this.totalEmails = res.data['messagesTotal'];
        this.totalThreads = res.data['threadsTotal'];
        this.email = res.data['emailAddress']
      })
    },
    async getFiltersByProvider() {
      await axios.post(import.meta.env.VITE_ROOT_API + "/filter/provider/" + this.$route.params.uuid).then((res) => {
        this.filterSet = res.data;
        if (res.data.length > 0) {
          this.filterSetId = res.data[0]['id'];
        }
      });
    },
    async getTotalPoints() {
      await axios.post(import.meta.env.VITE_ROOT_API + "/user/rewards/total/" + this.$route.params.uuid).then((res) => {
        this.total_points = res.data;
      })
    },
    async getAvailablePoints() {
      await axios.post(import.meta.env.VITE_ROOT_API + "/user/rewards/available/" + this.$route.params.uuid).then((res) => {
        this.available_points = res.data;
      })
    },
    async generateBin() {
      let data = {
        filter_id: this.filterSetId,
        uuid: this.$route.params.uuid
      }
      await axios.post(import.meta.env.VITE_ROOT_API + "/bin", data).then((res) => {
        this.$router.push({path: "/bin/" + res.data.id})
      });
    },
    setCircle(isTrue, index) {
      if (isTrue && index === '') {
        return "bg-indigo-500 text-white"
      } else if (!isTrue && index === '') {
        return "border-2 border-indigo-500 text-black"
      }

      if (isTrue && this.steps[index] === true) {
        return "bg-indigo-500 text-white"
      } else if (!isTrue && this.steps[index] === true) {
        return "border-2 border-indigo-500 text-black"
      } else {
        return "border-2 border-gray-200 text-black"
      }
    },
    setProgressWidth(isTrue, index) {
      if (isTrue && this.steps[index] === true) {
        return "width: 100%;";
      } else if (!isTrue && this.steps[index] === true) {
        return "width: 40%;";
      } else {
        return "width: 0%;";
      }
    },
    setProgressColor(isTrue, index) {
      if (isTrue && this.steps[index] === true) {
        return "bg-indigo-500"
      } else if (!isTrue && this.steps[index] === true) {
        return "bg-indigo-300"
      } else {
        return "bg-gray-200";
      }
    },
    setCardBorder(index) {
      console.log(this.steps['amount_true'])
      if (index === this.steps['amount_true']) {
        return "border-2 border-indigo-500"
      } else if (index > this.steps['amount_true']) {
        return "disabled opacity-50 cursor-not-allowed border-gray-200 "
      } else if (this.steps['amount_true'] === 4) {
        return "border-1 border-green-500"
      }
    }
  }
}
</script>

<template>
  <div class="loading" v-if="!this.loaded">
    <svg class="animate-spin -ml-1 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
      <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
      <path class="opacity-75" fill="currentColor"
            d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
    </svg>
  </div>
  <div id="selected-provider" class="w-full" v-if="this.loaded">
    <div class="selected-provider-header">
      <div class="container mx-auto px-4">
        <router-link to="/provider" class="cursor-pointer text-sm pt-5 ">
          <button type="button"
                  class="rotate-180 text-white bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:ring-blue-300 font-medium rounded-full text-sm p-2.5 text-center inline-flex items-center mr-2 dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800">
            <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg">
              <path fill-rule="evenodd"
                    d="M10.293 3.293a1 1 0 011.414 0l6 6a1 1 0 010 1.414l-6 6a1 1 0 01-1.414-1.414L14.586 11H3a1 1 0 110-2h11.586l-4.293-4.293a1 1 0 010-1.414z"
                    clip-rule="evenodd"></path>
            </svg>
          </button>
          Ga terug
        </router-link>
        <div class="flex flex-wrap pt-5 w-full mb-10 flex-col items-center text-center">
          <h1 class="sm:text-3xl text-2xl font-medium title-font mb-2 text-gray-800">
            {{ this.email }}</h1>
          <p class="lg:w-1/2 w-full leading-relaxed text-gray-500">Gmail</p>
        </div>
      </div>
    </div>
    <div class="text-gray-600 body-font">
      <div class="container mx-auto px-4">

        <div class="w-full py-6">
          <div class="flex">
            <div class="w-1/4">
              <div class="relative mb-2">
                <div class="w-10 h-10 mx-auto rounded-full text-lg text-white flex items-center"
                     :class="setCircle(this.steps['has_filters'], '', this.steps['amount_true'])">
                  <span span class="text-center w-full">1</span>
                </div>
              </div>

              <div class="text-xs text-center md:text-base">Maak filter</div>
            </div>
          
            <div class="w-1/4">
              <div class="relative mb-2">
                <div class="absolute flex align-center items-center align-middle content-center"
                     style="width: calc(100% - 2.5rem - 1rem); top: 50%; transform: translate(-50%, -50%)">
                  <div class="w-full bg-gray-200 rounded items-center align-middle align-center flex-1">
                    <div class="w-0 py-1 rounded" :class="setProgressColor(this.steps['has_bins'], 'has_filters')"
                         :style="setProgressWidth(this.steps['has_bins'], 'has_filters')"></div>
                  </div>
                </div>

                <div class="w-10 h-10 mx-auto rounded-full text-lg  flex items-center"
                     :class="setCircle(this.steps['has_bins'],  'has_filters', this.steps['amount_true'] )">
                   <span class="text-center w-full">2</span>
                </div>
              </div>

              <div class="text-xs text-center md:text-base">Verwijder emails</div>
            </div>

            <div class="w-1/4">
              <div class="relative mb-2">
                <div class="absolute flex align-center items-center align-middle content-center"
                     style="width: calc(100% - 2.5rem - 1rem); top: 50%; transform: translate(-50%, -50%)">
                  <div class="w-full bg-gray-200 rounded items-center align-middle align-center flex-1">
                    <div class="w-0  py-1 rounded" :class="setProgressColor(this.steps['has_statistics'], 'has_bins')"
                         :style="setProgressWidth(this.steps['has_statistics'], 'has_bins')"></div>
                  </div>
                </div>

                <div
                    class="w-10 h-10 mx-auto bg-white border-2 border-gray-200 rounded-full text-lg text-white flex items-center"
                    :class="setCircle(this.steps['has_statistics'],  'has_bins', this.steps['amount_true'])">
                  <span class="text-center w-full">3</span>
                </div>
              </div>

              <div class="text-xs text-center md:text-base">Bekijk statistieken</div>
            </div>

            <div class="w-1/4">
              <div class="relative mb-2">
                <div class="absolute flex align-center items-center align-middle content-center"
                     style="width: calc(100% - 2.5rem - 1rem); top: 50%; transform: translate(-50%, -50%)">
                  <div class="w-full bg-gray-200 rounded items-center align-middle align-center flex-1">
                    <div class="w-0  py-1 rounded"
                         :class="setProgressColor(this.steps['has_rewards'], 'has_statistics')"
                         :style="setProgressWidth(this.steps['has_rewards'], 'has_statistics')"></div>
                  </div>
                </div>

                <div
                    class="w-10 h-10 mx-auto bg-white border-2 border-gray-200 rounded-full text-lg text-white flex items-center"
                    :class="setCircle(this.steps['has_rewards'],  'has_statistics', this.steps['amount_true'])">
                  <span class="text-center  w-full">4</span>
                </div>
              </div>

              <div class="text-xs text-center md:text-base">Verzilver rewards</div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="text-gray-600 body-font mb-8" v-if="steps['amount_true'] === 4">
      <div class="container mx-auto px-4">
        <p class="text-sm text-center text-green-500">Je hebt alle stappen voltooid.</p>
      </div>
    </div>
    <div class="text-gray-600 body-font pb-20">
      <div class="container mx-auto px-4">
        <div class="flex flex-wrap -m-4">
          <div class="w-full p-4 lg:w-1/4 card">
            <div class="flex border-2 rounded-2xl border-opacity-50 p-8 flex-col"
                 :class="this.setCardBorder(0)">
              <div class="flex-grow">
                <h2 class="text-gray-800 text-xl title-font font-medium mb-3">Filters</h2>
                <p class="leading-relaxed text-xs text-block">Stel filters in om je mailbox op te schonen!</p>
              </div>
              <div class="buttons">
                <p v-if="this.filterSet.length > 0" class="text-xs text-gray-500">Selecteer een filter</p>
                <select v-if="this.filterSet.length > 0" v-model="filterSetId" class="mb-4  mt-4 block w-full">
                  <option v-for="filterSet in this.filterSet" :value="filterSet['id']">Filter {{ filterSet['id'] }}
                    {{ filterSet['name'] !== "" ? "- " + filterSet['name'] : "" }}
                  </option>
                </select>
                <button v-if="this.filterSet.length > 0" @click="updateFilter()"
                        class="mt-4 w-full text-indigo-500 border-2 border-indigo-500 py-2 px-5 focus:outline-none hover:bg-indigo-500 hover:text-white rounded-2xl text-sm">
                  Update filter
                </button>
                <RouterLink to="/filter"
                            class="mt-4 w-full text-center text-white bg-indigo-500 border-0 py-2 px-5 focus:outline-none hover:border-indigo-500 hover:bg-indigo-600 rounded-2xl text-sm">
                  Maak filter
                </RouterLink>
              </div>
            </div>
          </div>
          <div class="w-full p-4 lg:w-1/4 card">
            <div class="flex border-2 rounded-2xl border-opacity-50 p-8 flex-col "
                 :class="this.setCardBorder(1)">
              <div class="flex-grow">
                <h2 class="text-gray-900 text-lg title-font font-medium mb-3">Synchroniseren</h2>
                <p class="leading-relaxed text-xs text-block">Synchroniseer de ingestelde filters met je mailbox, dit
                  kan
                  handmatig
                  of automatisch </p>
              </div>
              <div class="buttons">
                <p v-if="this.filterSet.length > 0" class="text-xs text-gray-500">Selecteer een filter</p>
                <select v-if="this.filterSet.length > 0" v-model="filterSetId" class="mb-4  mt-4 block w-full">
                  <option v-for="filterSet in this.filterSet" :value="filterSet['id']">Filter {{ filterSet['id'] }}
                    {{ filterSet['name'] !== "" ? "- " + filterSet['name'] : "" }}
                  </option>
                </select>
                <button
                    @click="generateBin()"
                    class="mt-4 w-full text-white bg-indigo-500 border-0 py-2 px-5 focus:outline-none hover:bg-indigo-600 rounded-2xl text-sm">
                  Synchroniseer
                </button>
              </div>
            </div>
          </div>
          <div class="w-full p-4 lg:w-1/4 card">
            <div class="flex border-2 rounded-2xl border-opacity-50 p-8 flex-col"
                 :class="this.setCardBorder(2)">
              <div class="flex-grow">
                <h2 class="text-gray-900 text-lg title-font font-medium mb-3">Statistieken</h2>
                <p class="leading-relaxed text-xs text-block">Bekijk je energy besparingen, level en grafieken</p>
              </div>
              <div class="buttons">
                <RouterLink :to="this.$route.path + '/statistics'"
                            class="mt-4 w-full text-center text-white bg-indigo-500 border-0 py-2 px-5 focus:outline-none hover:bg-indigo-600 rounded-2xl text-sm">
                  Bekijk je statistieken
                </RouterLink>
              </div>
            </div>
          </div>
          <div class="w-full p-4 lg:w-1/4 card">
            <div class="flex border-2 rounded-2xl border-opacity-50 p-8 flex-col "
                 :class="this.setCardBorder(3)">
              <div class="flex-grow">
                <h2 class="text-gray-900 text-lg title-font font-medium mb-3">Rewards</h2>
                <p class="leading-relaxed text-xs">Bekijk en verzilver hier je gespaarde punten!</p>
                <br>

              </div>
              <div class="buttons">
                <p class="leading-relaxed text-sm">Nog te besteden punten: <span
                    class="font-bold">{{ this.available_points }}</span></p>
                <p class="leading-relaxed text-sm">Verzilverde punten: <span class="font-bold">{{
                    this.total_points
                  }}</span></p>
                <button v-if="available_points <= 0"
                        disabled
                        class="opacity-50 mt-4 w-full text-white bg-indigo-500 border-0 py-2 px-5 focus:outline-none hover:bg-indigo-600 rounded-2xl text-sm">
                  Nog niet genoeg punten
                </button>
                <button v-if="total_points <= 0"
                        disabled
                        class="opacity-50 mt-4 w-full text-white bg-indigo-500 border-0 py-2 px-5 focus:outline-none hover:bg-indigo-600 rounded-2xl text-sm">
                  Nog geen verzilverde punten
                </button>
                <RouterLink v-if="available_points > 0" :to="this.$route.path + '/rewards'"
                            class="mt-4 w-full text-center text-white bg-indigo-500 border-0 py-2 px-5 focus:outline-none hover:bg-indigo-600 rounded-2xl text-sm">
                  Punten verzilveren
                </RouterLink>
                <RouterLink v-if="total_points > 0" :to="this.$route.path + '/map'"
                            class="text-center mt-4 w-full text-white bg-indigo-500 border-0 py-2 px-5 focus:outline-none hover:bg-indigo-600 rounded-2xl text-sm">
                  Rewards bekijken
                </RouterLink>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.card {
  height: 500px;

}
.card > div {
  height: 100%;
  box-shadow: 0px 4px 4px rgba(0, 0, 0, .05);
}
.loading {
  position: absolute;
  left: 50%;
  top: 50%;
  text-align: center;
  display: flex;
  align-items: center;
  flex-direction: column;
  justify-content: center;
  transform: translate(-50%, -50%);
}

.loading .animate-spin {
  color: black !important;
  width: 50px;
}
</style>
