<script setup>
import { inject } from 'vue'
defineProps({
  handleSubmit: {
    type: Function,
    required: true
  }
})

let store = inject('storeProvider', {})
</script>

<template>
  <div class="form_container">
    <form @submit.prevent="handleSubmit" class="form" method="post">
      <div
        :style="{
          background: store.state.feedback.status === 200 ? 'green' : 'red',
          display: store.state.feedback.status == null ? 'none' : 'block'
        }"
        class="feedback"
      >
        {{ store.state.feedback.msg }}
      </div>
      <slot> </slot>
    </form>
  </div>
</template>

<style lang="scss">
@import '@/assets/scss/mixins';

.form_container {
  width: 80%;
  padding: 20px 0;
  @include horizon-center;

  form {
    @include horizon-center;
    @include perfect-center;
    flex-direction: column;
    color: var(--color-primary-light);
    font-size: 16px;
    gap: 0.3em;
    padding: 2em;
    background: var(--section-background-light);
    border-radius: 7px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.05);
    width: 50%;

    .close-wrapper {
      width: 100%;
      display: flex;
      justify-content: flex-end;

      i {
        cursor: pointer;
      }
    }

    .field-wrapper {
      width: 90%;
      margin: 7px 0;

      .avatar-wrapper {
        @include perfect-center;

        label {
          position: relative;
          width: 9rem;
          height: 9rem;
          border-radius: 50%;
          cursor: pointer;

          img {
            position: absolute;
            top: 0;
            width: 100%;
            height: 100%;
            border-radius: 50%;
          }

          .action-wrapper {
            position: absolute;
            top: 0;
            width: 9rem;
            height: 9rem;
            border-radius: 50%;
            @include perfect-center;
            background: transparent;
            transition: 0.3s ease-in;
            color: var(--color-text-primary);

            i {
              opacity: 0;
            }

            &:hover {
              background: #18181880;
            }

            &:hover i {
              opacity: 1;
            }
          }
        }
      }

      .input-wrapper {
        position: relative;

        i {
          @include perfect-center;
          opacity: 0.7;
          position: absolute;
          width: 10%;
          height: 100%;
          top: 0;
          left: 0;
          text-align: center;
          color: var(--color-text-primary);
          background: var(--primary-background);
        }

        label {
          text-align: left;
          opacity: 0.75;
        }

        input {
          border: 0.08px solid #7a7f9a45;
          border-radius: 3px;
          background: #fff;
          padding: 15px;
          width: 100%;
          height: 100%;
          padding-left: 11%;
        }
      }

      .check {
        display: flex;
        align-items: center;
        gap: 0;

        input {
          margin-right: 10px;
        }

        label {
          font-size: 16px !important;
        }
      }
    }

    input[type='submit'] {
      background: var(--color-primary);
      color: var(--secondary-background);
      border: none;
      padding: 12px;
      text-align: center;
      cursor: pointer;
    }
  }

  p {
    text-align: center;
    margin-top: 20px;
    color: var(--color-text-secondary);
  }
}

@media (max-width: 1400px) {
  form {
    width: 90% !important;
  }
}
</style>
